package game;

public enum Mission {
    SCORE("Reach 50 points in 45 seconds", 45, 50, MissionType.SCORE),
    SURVIVE_60("Survive for 60 seconds", 60, 0, MissionType.SURVIVAL),
    NO_BOMBS("Complete without clicking bombs", 60, 0, MissionType.NO_BOMBS);

    public final String description;
    public final int timeLimit;
    public final int targetValue;
    public final MissionType type;

    Mission(String description, int timeLimit, int targetValue, MissionType type) {
        this.description = description;
        this.timeLimit = timeLimit;
        this.targetValue = targetValue;
        this.type = type;
    }

    public enum MissionType {
        SCORE, SURVIVAL, NO_BOMBS
    }
}