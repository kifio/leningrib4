package kifio.leningrib.model.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import kifio.leningrib.Utils;
import kifio.leningrib.model.pathfinding.ForestGraph;
import kifio.leningrib.screens.GameScreen;

public class Forester extends MovableActor {

	private static final int AREA_SIDE = 5;

	private final String running;
	private final String idle;
	private final Vector2[] area = new Vector2[AREA_SIDE * AREA_SIDE];  // TODO: Maybe replace with one large
	// TODO: rectangle.

	public void updateArea() {
		int x = (int) Utils.mapCoordinate(getX());
		int y = (int) Utils.mapCoordinate(getY());

		x /= GameScreen.tileSize;
		y /= GameScreen.tileSize;

		x -= 2;
		y -= 2;

		for (int i = 0; i < area.length; i++) {
			area[i].x = x * GameScreen.tileSize;
			area[i].y = y * GameScreen.tileSize;

			x++;
			if ((i + 1) >= (AREA_SIDE - 1) && (i + 1) % AREA_SIDE == 0) {
				y++;
				x -= AREA_SIDE;
			}
		}
	}

	public Vector2[] getArea() {
		return area;
	}

	public void setPathDirectly(Vector2 vector2) {
		path.clear();
		path.add(vector2);
	}

	private enum MovingState {
		FORWARD, BACK, PURSUE, STOP
	}

	private float originalFromX, originalFromY, originalToX, originalToY;
	private int originalBottomLimit, originalTopLimit;
	private Array<Vector2> patrolRectangle = null;
	private int routePointFrom, routePointTo;
	private MovingState movingState = MovingState.FORWARD;
	private float stoppingTime = 0f;

	// Лесники начинают с патрулирования леса, поэтому у них две координаты
	public Forester(float originalFromX, float originalFromY, float originalToX, float originalToY, int index,
		ForestGraph forestGraph, int originalBottomLimit, int originalTopLimit) {
		super(originalFromX, originalFromY);
		this.originalFromX = originalFromX;
		this.originalFromY = originalFromY;
		this.originalToX = originalToX;
		this.originalToY = originalToY;
		this.originalBottomLimit = originalBottomLimit;
		this.originalTopLimit = originalTopLimit;

		running = String.format(Locale.getDefault(), "enemy_%d_run.txt", index);
		idle = String.format(Locale.getDefault(), "enemy_%d_idle.txt", index);
		for (int i = 0; i < area.length; i++) { area[i] = new Vector2(); }
		setPath(originalToX, originalToY, forestGraph);
	}

//    public Forester(Array<Vector2> routePoints) {
//        super(routePoints.get(0));
//        this.patrolRectangle = routePoints;
//        routePointFrom = 0;
//        routePointTo = 1;
//        setPatrolRoute(routePoints.get(routePointFrom), routePoints.get(routePointTo));
//    }

//    private void resetPatrolRoute() {
//        stop();
//        if (movingState == MovingState.FORWARD) {
//            if (patrolRectangle != null) {
//                if (routePointTo == patrolRectangle.size - 1) {
//                    routePointTo = 0;
//                    routePointFrom = patrolRectangle.size - 1; // routePointTo++;
//                } else if (routePointTo == 0) {
//                    routePointTo++;
//                    routePointFrom = 0; // routePointTo++;
//                } else {
//                    routePointTo++;
//                    routePointFrom++;
//                }
//                setPatrolRoute(patrolRectangle.get(routePointFrom), patrolRectangle.get(routePointTo));
//            } else {
//                movingState = MovingState.BACK;
//                setPatrolRoute(to, from);
//            }
//        } else if (movingState == MovingState.BACK) {
//            movingState = MovingState.FORWARD;
//            setPatrolRoute(from, to);
//        }
//    }

//	public void setPatrolRoute(Vector2 from, Vector2 to) {
//        SequenceAction seq = new SequenceAction();
//
//        if (from == null || to == null) {
//            Gdx.app.log("kifio", "something wrong!");
//        }
//
//        float dy = Math.abs(to.y - from.y);
//        float dx = Math.abs(to.x - from.x);
//        int steps = 0;
//
//        if (dy > dx) {
//            steps = (int) (dy / GameScreen.tileSize);
//            if (to.y < from.y) {
//                for (int i = 0; i <= steps; i++) {
//                    path.add(new Vector2(from.x, (from.y - GameScreen.tileSize * i)));
//                }
//            } else {
//                for (int i = 0; i <= steps; i++) {
//                    path.add(new Vector2(from.x, (from.y + GameScreen.tileSize * i)));
//                }
//            }
//        } else {
//            steps = (int) (dx / GameScreen.tileSize);
//            if (to.x < from.x) {
//                for (int i = 0; i <= steps; i++) {
//                    path.add(new Vector2((from.x - GameScreen.tileSize * i), from.y));
//                }
//            } else {
//                for (int i = 0; i <= steps; i++) {
//                    path.add(new Vector2((from.x + GameScreen.tileSize * i), from.y));
//                }
//            }
//        }
//
//        float fromX = getX();
//        float fromY = getY();
//
//        for (int i = 0; i < path.size(); i++) {
//            Vector2 vec = path.get(i);
//            seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y, getVelocity()));
//            seq.addAction(getDelayAction(0.2f));
//            fromX = vec.x;
//            fromY = vec.y;
//        }
//
//        seq.addAction(getCompleteAction());
//        addAction(seq);
//	}

//    private Action getCompleteAction() {
//        return new Action() {
//            @Override
//            public boolean act(float delta) {
//                resetPatrolRoute();
//                return false;
//            }
//        };
//    }

	// Вычисляет путь лесника от a, до b.
	public void setPath(float tx, float ty, ForestGraph forestGraph) {
		stop();

		forestGraph.updatePath(Utils.mapCoordinate(getX()), Utils.mapCoordinate(getY()),
			Utils.mapCoordinate(tx), Utils.mapCoordinate(ty), this.path);

		// Первая точка пути совпадает с координатами игрока,
		// чтобы лесник не стоял на месте лишнее время ее из пути удаляем.
//		int start = path.getCount() > 1 ? 1 : 0;
		/*for (int i = start; i < path.getCount(); i++) {
			this.path.add(new Vector2(path.get(i)));
		}*/

		addAction(getMoveActionsSequence(forestGraph));
	}

	public void updateMovementState(Player player, float delta, ForestGraph forestGraph) {

		float px = player.getX();
		float py = player.getY();

		if (Utils.isOverlapsWithVector(area, (int) px, (int) py)) {
			setPlayerNoticed();
		} else if (movingState == MovingState.PURSUE) {
			stopPursuing();
		} else if (movingState == MovingState.STOP) {
			if (stoppingTime < 2f) {
				stoppingTime += delta;
			} else {
				stoppingTime = 0f;
				startPatroling();
				setNewPath(forestGraph);
			}
		}

		if (isPursuePlayer()) {
			setPath(player.bounds.x, player.bounds.y, forestGraph);
		}
	}

	private void setPlayerNoticed() {
		stop();
		movingState = MovingState.PURSUE;
	}

	private void stopPursuing() {
		stop();
		movingState = MovingState.STOP;
	}

	private void startPatroling() {
		stop();
		movingState = MovingState.FORWARD;
	}

//    private void startPatrol(Vector2 from) {
//        stoppingTime = 0f;
//        movingState = MovingState.FORWARD;
//        setPath(from, to);
//    }

//    private Color commonColor = new Color(1f, 1f, 1f, 1f);

//    @Override
//    public void draw(Batch batch, float alpha) {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        super.draw(batch, alpha);
//        batch.setColor(commonColor);
//    }

	public SequenceAction getMoveActionsSequence(final ForestGraph forestGraph) {
		SequenceAction seq = new SequenceAction();
		float fromX = getX();
		float fromY = getY();

		int count = path.getCount();
		int i = count > 1 ? 1 : 0;

		for (; i < path.getCount(); i++) {
			Vector2 vec = path.get(i);
			seq.addAction(getMoveAction(fromX, fromY, vec.x, vec.y));
			seq.addAction(getDelayAction(getDelayTime()));
			fromX = vec.x;
			fromY = vec.y;
		}

		seq.addAction(getDelayAction(getDelayTime()));
		if (movingState.equals(MovingState.FORWARD)) {
			seq.addAction(Actions.run(new Runnable() {
				@Override public void run() {
					setNewPath(forestGraph);
				}
			}));
		}

		return seq;
	}

	private void setNewPath(ForestGraph forestGraph) {
		int toX = bounds.x == (int) originalToX ? (int) originalFromX : (int) originalToX;
		int toY = ThreadLocalRandom.current().nextInt(originalBottomLimit, originalTopLimit) * GameScreen.tileSize;

		setPath(toX, toY, forestGraph);
	}

	public boolean isPursuePlayer() {
		return movingState == MovingState.PURSUE;
	}

	public float getVelocity() {
		return 800f;
	}

	@Override protected float getDelayTime() {
		return 0.1f;
	}

	@Override protected String getIdlingState() {
		return running;
	}

	@Override protected String getRunningState() {
		return idle;
	}

	@Override public float getFrameDuration() {
		return 0.1f;
	}
}
