package main;

import java.io.Serializable;

public class Action implements Serializable {
    private ActionType actionType;
    private String actionArgument;

    public Action(ActionType actionType) {
        this.actionType = actionType;
    }

    public Action(ActionType actionType, String actionArgument) {
        this.actionType = actionType;
        this.actionArgument = actionArgument;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getActionArgument() {
        return actionArgument;
    }

    public void setActionArgument(String actionArgument) {
        this.actionArgument = actionArgument;
    }
}
