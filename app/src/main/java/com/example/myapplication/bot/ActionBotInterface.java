package com.example.myapplication.bot;

public interface ActionBotInterface {
    public boolean getStateResult();
    public void doAction();
    public boolean didAction();
    public String getName();
    public void setDidAction(boolean actionbooldid);
    public void errorActiom(String message);
}
