package com.beanshogi.gui.listeners;

import java.util.List;

import com.beanshogi.model.CheckEvent;

public interface KingCheckListener {
    void onKingInCheck(List<CheckEvent> checkEvents);
}
