package com.michaelhitchens.hackathon.hangman;

enum Difficulty {
    EASY("EASY", R.raw.easy, R.raw.easyh),
    NORMAL("NORMAL", R.raw.normal, R.raw.normalh),
    HARD("HARD", R.raw.hard, R.raw.hardh);

    private final String difficultyString;
    private final int fileId;
    private final int headerId;
    Difficulty(String str, int fileId, int headerId) {
        this.difficultyString = str;
        this.fileId = fileId;
        this.headerId = headerId;
    }

    public String toString() {
        return difficultyString;
    }

    public int getFileId() {
        return fileId;
    }

    public int getHeaderId() {
        return headerId;
    }
}
