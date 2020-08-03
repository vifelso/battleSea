package com.byril.hryharenka.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.byril.hryharenka.Cell;
import com.byril.hryharenka.SeaBattle;
import com.byril.hryharenka.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GameScreen implements Screen {
    public static int HEIGHT_INDENT = 250;
    public static int WIDTH_INDENT = 250;
    private static int SHIP_1_DECK_SIZE = 4;
    private static int SHIP_2_DECK_SIZE = 3;
    private static int SHIP_3_DECK_SIZE = 2;
    private static int SHIP_4_DECK_SIZE = 1;
    private static Texture CELL_TEXTURE = new Texture(Gdx.files.internal("cell.png"));
    private static Texture BOARD_TEXTURE = new Texture(Gdx.files.internal("board.png"));
    private static final Texture DECK_TEXTURE = new Texture(Gdx.files.internal("deck.png"));
    private Image board;
    private final Texture shaderTexture;
    private int shaderXPosition;
    private int shaderYPosition;
    private Vector3 convertedCoord = new Vector3(0, 0, 0);
    boolean showTextureRegion = false;


    private Viewport viewport;
    private Stage stage;
    private Button placeBtn;
    private List<Cell> cells = new ArrayList<>();
    private List<Cell> shipArea = new ArrayList<>();
    private List<Ship> ships = new ArrayList<>();
    private ShaderProgram shaderProgram;
    private SpriteBatch batch;

    public GameScreen() {
        ShaderProgram.pedantic = false;
        String vertexShader = Gdx.files.internal("vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("fragment.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled()) {
            Gdx.app.log("Problem loading shader:", shaderProgram.getLog());
        }
        batch = new SpriteBatch();

        createShips();
        Camera camera = new OrthographicCamera(SeaBattle.WIDTH, SeaBattle.HEIGHT);
        viewport = new FitViewport(SeaBattle.WIDTH, SeaBattle.HEIGHT, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        for (int i = 0; i < 100; i++) {
            cells.add(new Cell(CELL_TEXTURE, "cell_" + i, i));
            stage.addActor(cells.get(i));
        }
        placeBtn = new Button(new SpriteDrawable(new Sprite(new TextureRegion(new Texture(Gdx.files.internal("place.png"))))));
        placeBtn.setPosition(500 - placeBtn.getPrefWidth() / 2, 0);
        placeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTextureRegion = false;
                resetPlacing();
                placeShips();

                ships.stream()
                        .map(Ship::getCellIds)
                        .flatMap(Collection::stream)
                        .distinct()
                        .forEach(id -> {
                            Cell cell = cells.get(id);
                            cell.getSprite().setTexture(DECK_TEXTURE);
                        });
            }
        });
        board = new Image(new Texture(Gdx.files.internal("board.png")));
        board.setPosition(185, 235);
        stage.addActor(board);
        stage.addActor(placeBtn);
        viewport.apply();
        shaderTexture = new Texture(Gdx.files.internal("shader.png"));
        Gdx.input.justTouched();
    }

    private void resetPlacing() {
        cells.forEach(cell -> cell.getSprite().setTexture(CELL_TEXTURE));
        shipArea = new ArrayList<>();
        ships.forEach(ship -> {
            ship.setCellIds(new ArrayList<>());
        });
    }

    private void createShips() {
        for (int i = 0; i < SHIP_4_DECK_SIZE; i++) {
            ships.add(new Ship(i, 4));
        }
        for (int i = 0; i < SHIP_3_DECK_SIZE; i++) {
            ships.add(new Ship(i, 3));
        }
        for (int i = 0; i < SHIP_2_DECK_SIZE; i++) {
            ships.add(new Ship(i, 2));
        }
        for (int i = 0; i < SHIP_1_DECK_SIZE; i++) {
            ships.add(new Ship(i, 1));
        }
    }


    private void placeShips() {
        for (Ship ship : ships) {
            ship.setOrientation(ThreadLocalRandom.current().nextInt(Ship.HORIZONTAL, Ship.VERTICAL + 1));
            Set<Integer> excluded = new LinkedHashSet<>();
            int random;
            do {
                random = getRandomWithExclusion(ThreadLocalRandom.current(), 0, 10, excluded);
                excluded.add(random);
            } while (!canArrange(random, ship));
        }
    }

    private boolean canArrange(int nextInt, Ship ship) {
        boolean rez = false;

        int orientation = ship.getOrientation();
        int decks = ship.getDecks();
        Set<Integer> excluded = new LinkedHashSet<>();
        int sinceCell;
        int limit = 10 - decks + 1;
        do {
            if (excluded.size() >= limit) {
                break;
            }
            sinceCell = getRandomWithExclusion(ThreadLocalRandom.current(), 0, limit, excluded);
            int freeCounter = 0;
            if (orientation == Ship.HORIZONTAL) {
                for (int i = sinceCell; i < decks + sinceCell; i++) {
                    if (!shipArea.contains(cells.get(nextInt * 10 + i))) {
                        freeCounter++;
                    } else {
                        excluded.add(sinceCell);
                        break;
                    }
                    if (freeCounter >= decks) {
                        for (int j = i; j > i - decks; j--) {
                            int cellId = nextInt * 10 + j;
                            if (!shipArea.contains(cells.get(cellId))) {
                                shipArea.add(cells.get(cellId));
                            }
                            addNearCells(cellId);
                            ship.getCellIds().add(cellId);
                        }
                        rez = true;
                        break;
                    } else {
                        rez = false;
                    }

                }
            } else {
                for (int i = sinceCell; i < decks + sinceCell; i++) {
                    if (!shipArea.contains(cells.get(i * 10 + nextInt))) {
                        freeCounter++;
                    } else {
                        excluded.add(sinceCell);
                        break;
                    }
                    if (freeCounter >= decks) {
                        for (int j = i; j > i - decks; j--) {
                            int cellId = j * 10 + nextInt;
                            if (!shipArea.contains(cells.get(cellId))) {
                                shipArea.add(cells.get(cellId));
                            }
                            addNearCells(cellId);
                            ship.getCellIds().add(cellId);
                        }
                        rez = true;
                        break;
                    } else {
                        rez = false;
                    }

                }
            }
        } while (!rez);

        return rez;
    }

    private void addNearCells(int idCell) {
        //для простоты расчета близлежащих ячеек этот метод находится здесь
        //в дальнейшем, лучше перенести егo в com.byril.hryharenka.Ship и строить граф там
        int horizontalPlace = idCell % Cell.CELL_WIDTH_ROWS;
        int verticalPlace = idCell / Cell.CELL_HEIGHT_ROWS;
        if (horizontalPlace > 0) {
            int leftHorizontalCellId = horizontalPlace - 1;
            if (!shipArea.contains(cells.get(verticalPlace * Cell.CELL_HEIGHT_ROWS + horizontalPlace - 1))) {
                shipArea.add(cells.get(verticalPlace * Cell.CELL_HEIGHT_ROWS + leftHorizontalCellId));
            }
            if (verticalPlace > 0 && !shipArea.contains(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + leftHorizontalCellId))) {
                shipArea.add(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + leftHorizontalCellId));
            }
            if (verticalPlace < Cell.CELL_HEIGHT_ROWS - 1 && !shipArea.contains(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + leftHorizontalCellId))) {
                shipArea.add(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + leftHorizontalCellId));
            }
        }
        if (horizontalPlace < Cell.CELL_WIDTH_ROWS - 1) {
            int rightHorizontalCellId = horizontalPlace + 1;
            if (!shipArea.contains(cells.get(verticalPlace * Cell.CELL_HEIGHT_ROWS + horizontalPlace + 1))) {
                shipArea.add(cells.get(verticalPlace * Cell.CELL_HEIGHT_ROWS + rightHorizontalCellId));
            }
            if (verticalPlace > 0 && !shipArea.contains(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + rightHorizontalCellId))) {
                shipArea.add(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + rightHorizontalCellId));
            }
            if (verticalPlace < Cell.CELL_HEIGHT_ROWS - 1 && !shipArea.contains(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + rightHorizontalCellId))) {
                shipArea.add(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + rightHorizontalCellId));
            }
        }

        if (verticalPlace > 0 && !shipArea.contains(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + horizontalPlace))) {
            shipArea.add(cells.get((verticalPlace - 1) * Cell.CELL_HEIGHT_ROWS + horizontalPlace));
        }

        if (verticalPlace < Cell.CELL_HEIGHT_ROWS - 1 && !shipArea.contains(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + horizontalPlace))) {
            shipArea.add(cells.get((verticalPlace + 1) * Cell.CELL_HEIGHT_ROWS + horizontalPlace));
        }
    }

    public int getRandomWithExclusion(ThreadLocalRandom rnd, int start, int end, Set<Integer> exclude) {
        int range = end - start;
        int random = rnd.nextInt(range);
        while (exclude.contains(random) && exclude.size() < end) {
            random = rnd.nextInt(range);
        }
        return random;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(242 / 255f, 242 / 255f, 242 / 255f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.justTouched()) {
            shaderXPosition = Gdx.input.getX();
            shaderYPosition = Gdx.input.getY();

            convertedCoord = new Vector3(shaderXPosition, shaderYPosition, 0);
            viewport.getCamera().unproject(convertedCoord);
            showTextureRegion = true;
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (showTextureRegion) {
            batch.begin();
            batch.setShader(shaderProgram);
            batch.draw(shaderTexture, convertedCoord.x * 2, convertedCoord.y);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        shaderTexture.dispose();
        stage.dispose();
    }
}
