package com.beanshogi.gui.listeners;

import java.util.List;

import com.beanshogi.core.board.Move;
import com.beanshogi.core.board.MoveManager;
import com.beanshogi.core.game.CheckEvent;
import com.beanshogi.core.game.Controller;
import com.beanshogi.core.game.Player;
import com.beanshogi.core.game.Sides;
import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.listeners.event.GameEventListener;
import com.beanshogi.gui.listeners.event.GameStatsListener;
import com.beanshogi.gui.listeners.event.KingCheckListener;
import com.beanshogi.gui.listeners.event.SoundEventListener;
import com.beanshogi.gui.listeners.event.UndoRedoListener;
import com.beanshogi.gui.listeners.ui.UIInteractionListener;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.ResultType;

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

	// Notification methods

	public void notifySideOnTurnChanged(Sides sideOnTurn) {
		statsListener.onSideOnTurnChanged(sideOnTurn);
	}

	public void notifyMoveCount(int moveCount) {
		statsListener.onMoveCountChanged(moveCount);
	}

	public void notifyKingCheck(List<CheckEvent> checkEvents) {
		kingCheckListener.onKingInCheck(checkEvents);
	}

	public void updateUndoRedoState(Controller controller, MoveManager moveManager, Player playerOnTurn) {
		undoRedoListener.gainController(controller);
		undoRedoListener.onUndoStackEmpty(moveManager.isUndoStackEmpty());
		undoRedoListener.onRedoStackEmpty(moveManager.isRedoStackEmpty());
		undoRedoListener.onTurnAdvance(playerOnTurn);
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
