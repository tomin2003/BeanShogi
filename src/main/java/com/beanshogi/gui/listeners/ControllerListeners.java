package com.beanshogi.gui.listeners;

import java.util.List;

import com.beanshogi.engine.MoveManager;
import com.beanshogi.game.Controller;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;
import com.beanshogi.model.CheckEvent;
import com.beanshogi.model.Move;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;
import com.beanshogi.util.Sides;

/**
 * Aggregator class for handling listeners used in the controller.
 */
public class ControllerListeners {
	private final GameStatsListener statsListener;
	private final UndoRedoListener undoRedoListener;
	private final KingCheckListener kingCheckListener;
	private final GameEventListener gameEventListener;
	private final UIInteractionListener uiInteractionListener;
	private final SoundEventListener soundEventListener;

	public ControllerListeners(
			GameStatsListener statsListener, 
			UndoRedoListener undoRedoListener, 
			KingCheckListener kingCheckListener,
			GameEventListener gameEventListener,
			UIInteractionListener uiInteractionListener,
			SoundEventListener soundEventListener) {
		this.statsListener = statsListener;
		this.undoRedoListener = undoRedoListener;
		this.kingCheckListener = kingCheckListener;
		this.gameEventListener = gameEventListener;
		this.uiInteractionListener = uiInteractionListener;
		this.soundEventListener = soundEventListener;
	}

	// ==================== Getters ====================
	public GameStatsListener getStatsListener() {
		return statsListener;
	}

	public UndoRedoListener getUndoRedoListener() {
		return undoRedoListener;
	}

	public KingCheckListener getKingCheckListener() {
		return kingCheckListener;
	}

	public GameEventListener getGameEventListener() {
		return gameEventListener;
	}

	public UIInteractionListener getUiInteractionListener() {
		return uiInteractionListener;
	}

	public SoundEventListener getSoundEventListener() {
		return soundEventListener;
	}

	// ==================== Notification Methods ====================
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

	public void notifyMoveMade(Move move, Sides side) {
		gameEventListener.onMoveMade(move, side);
		soundEventListener.onPieceMove();
	}

	public void notifyGameEnd(Sides winner, String reason, ResultType resultType, Entry entry) {
		gameEventListener.onGameEnd(winner, reason, resultType, entry);
		String displayName = (winner == null) ? "Draw" : entry.getWinnerName();
		uiInteractionListener.showGameOver(displayName, reason);
	}

	public void notifyGameStart() {
		gameEventListener.onGameStart();
	}

	public void notifyResignation(Sides resigningSide) {
		gameEventListener.onResignation(resigningSide);
	}

	public boolean requestPromotionDecision(Piece piece, Position from, Position to) {
		return uiInteractionListener.requestPromotionDecision(piece, from, to);
	}

	public Sides requestResignationSide(List<Sides> eligibleSides) {
		return uiInteractionListener.requestResignationSide(eligibleSides);
	}
}
