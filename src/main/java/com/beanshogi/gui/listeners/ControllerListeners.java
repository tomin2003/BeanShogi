package com.beanshogi.gui.listeners;

import java.util.List;

import com.beanshogi.engine.MoveManager;
import com.beanshogi.game.Controller;
import com.beanshogi.model.CheckEvent;
import com.beanshogi.util.Sides;

/**
 * Aggregator class for handling listeners used in the controller.
 */
public class ControllerListeners {
	private final GameStatsListener statsListener;
	private final UndoRedoListener undoRedoListener;
	private final KingCheckListener kingCheckListener;

	public ControllerListeners(GameStatsListener statsListener, UndoRedoListener undoRedoListener, KingCheckListener kingCheckListener) {
		this.statsListener = statsListener;
		this.undoRedoListener = undoRedoListener;
		this.kingCheckListener = kingCheckListener;
	}

	public GameStatsListener getStatsListener() {
		return statsListener;
	}

	public UndoRedoListener getUndoRedoListener() {
		return undoRedoListener;
	}

	public KingCheckListener getKingCheckListener() {
		return kingCheckListener;
	}

	public void notifySideOnTurnChanged(Sides sideOnTurn) {
		statsListener.onSideOnTurnChanged(sideOnTurn);
	}

	public void notifyMoveCount(int moveCount) {
		statsListener.onMoveCountChanged(moveCount);
	}

	public void notifyKingCheck(List<CheckEvent> checkEvents) {
		kingCheckListener.onKingInCheck(checkEvents);
	}

	public void updateUndoRedoState(Controller controller, MoveManager moveManager) {
		undoRedoListener.gainController(controller);
		undoRedoListener.onUndoStackEmpty(moveManager.isUndoStackEmpty());
		undoRedoListener.onRedoStackEmpty(moveManager.isRedoStackEmpty());
	}
}
