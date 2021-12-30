package org.jlee.bowling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Bowling {

    static class Frame {
        final Game game;
        final int frameIdx;
        int rolls = 0;
        int firstRoll = 0;
        int secondRoll = 0;
        int extraRoll = 0;

        Frame(Game game, int frameIdx) {
            this.game = game;
            this.frameIdx = frameIdx;
        }

        boolean isStrike() {
            return firstRoll == 10;
        }

        boolean isSpare() {
            return !isStrike() && firstRoll + secondRoll == 10;
        }

        boolean canRoll() {
            if (rolls == 0) {
                return true;
            } else if (rolls == 1) {
                return !isStrike() || frameIdx == 9;
            } else {
                return frameIdx == 9 && (isStrike() || isSpare()) && rolls == 2;
            }
        }

        boolean canTotal() {
            return total() != -1;
        }

        int total() {
            if (canRoll()) {
                return -1;
            }
            if (isStrike()) {
                if (frameIdx < 8) {
                    if (nextFrame().rolls + nextFrame().nextFrame().rolls < 2) {
                        return -1;
                    }
                    if (nextFrame().isStrike()) {
                        return 10 + 10 + nextFrame().nextFrame().firstRoll;
                    } else {
                        return 10 + nextFrame().firstRoll + nextFrame().secondRoll;
                    }
                } else if (frameIdx == 8) {
                    if (nextFrame().rolls < 2) {
                        return -1;
                    }
                    return 10 + nextFrame().firstRoll + nextFrame().secondRoll;
                } else {
                    return 10 + secondRoll + extraRoll;
                }
            } else if (isSpare()) {
                if (frameIdx < 9) {
                    if (nextFrame().rolls < 1) {
                        return -1;
                    }
                    return 10 + nextFrame().firstRoll;
                } else {
                    return 10 + extraRoll;
                }
            } else {
                return firstRoll + secondRoll;
            }
        }

        Frame nextFrame() {
            return game.frames[frameIdx + 1];
        }

        void score(int score) {
            if (!canRoll()) {
                throw new IllegalStateException("No more rolls allowed in this frame");
            }
            if (score < 0 || score > 10) {
                throw new IllegalArgumentException("Invalid score");
            }
            switch (rolls++) {
                case 0:
                    firstRoll = score;
                    break;
                case 1:
                    if (firstRoll + score > 10) {
                        if (frameIdx != 9 || !isStrike()) {
                            throw new IllegalStateException("Invalid score");
                        }
                    }
                    secondRoll = score;
                    break;
                case 2:
                    extraRoll = score;
                    break;
            }
        }

        void reset() {
            rolls = 0;
            firstRoll = 0;
            secondRoll = 0;
            extraRoll = 0;
        }

    }

    static class Game {
        int frameIdx = -1;
        final Frame[] frames = new Frame[10];

        Game() {
            for(int i = 0; i < 10; i++) {
                frames[i] = new Frame(this, i);
            }
        }

        Frame currentFrame() {
            return frames[frameIdx];
        }

        Frame nextFrame() {
            if (isGameOver()) {
                throw new IllegalStateException("Game is over");
            }
            return frames[++frameIdx];
        }

        boolean isGameOver() {
            return frameIdx == 9 && !currentFrame().canRoll();
        }

        int gameTotal() {
            int total = 0;
            for (int i = 0; i < 10; i++) {
                if (frames[i].canTotal()) {
                    total += frames[i].total();
                }
            }
            return total;
        }

    }

    static class Player {
        final String name;
        final Game game;

        Player(String name) {
            this.name = name;
            this.game = new Game();
        }
    }

    public static void main(String[] args) {
        singlePlayerGame(System.in, System.out);
    }

    static int getPins(Player p, BufferedReader in, Appendable out) throws IOException {
        Frame f = p.game.currentFrame();
        out.append("Player: " + p.name +
                " Frame: " + (f.frameIdx + 1) +
                " Roll: " + (f.rolls + 1) + "\n(Enter number of pins knocked down 0-10 and press enter)....     ");
        String l = in.readLine();
        try {
            int pins = Integer.parseInt(l);
            if (pins < 0 || pins > 10) {
                out.append(l).append(" is not between 1 and 10. Please enter again...\n");
                return -1;
            }
            if (f.rolls == 1) {
                if (pins + f.firstRoll > 10 && !(f.isStrike() && f.frameIdx == 9)) {
                    out.append(l).append(" is not a valid score. Please enter again...\n");
                    return -1;
                }
            }
            return pins;
        } catch (NumberFormatException ne) {
            out.append(l).append(" is not a number. Please enter again...\n");
            return -1;
        }
    }

    static Player newPlayer(BufferedReader in, Appendable out) throws IOException {
        out.append("Enter the players name and press enter...    ");
        String name = in.readLine();
        if (name == null || name.isEmpty()) {
            out.append("Name must not be empty");
            return null;
        }
        return new Player(name);
    }

    static void playerTakeTurn(Player p, BufferedReader in, Appendable out) throws IOException {
        Frame f = p.game.nextFrame();
        while (f.canRoll()) {
            int pins = getPins(p, in, out);
            if (pins != -1) {
                f.score(pins);
            }
            printScore(out, p);
        }
    }

    static void singlePlayerGame(InputStream in, Appendable out) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Player p = null;
            while (p == null) {
                p = newPlayer(br, out);
            }

            while (!p.game.isGameOver()) {
                playerTakeTurn(p, br, out);
            }

            out.append("Game Over\n");
        } catch (IOException e) {
            try {
                out.append("Game error");
                e.printStackTrace();
                System.exit(1);
            } catch (Exception ignore) {}
        }
    }

    final static int NAME_COL_W = 10;
    final static int SCORE_COL_W = 6;
    final static String HEADER = "|Player    |  1   |  2   |  3   |  4   |  5   |  6   |  7   |  8   |  9   |  10  |Total |\n";
    final static String DIVIDER = "|----------|------|------|------|------|------|------|------|------|------|------|------|\n";
    static void printScore(Appendable out, Player... players) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER);
        sb.append(DIVIDER);
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            sb.append('|');
            append(sb, p.name, NAME_COL_W);
            sb.append('|');

            //append rolls
            for (int j = 0; j < 10; j++) {
                Frame f = p.game.frames[j];
                append(sb, f, SCORE_COL_W);
                sb.append('|');
            }
            //skip total
            append(sb, "", SCORE_COL_W);
            sb.append('|');
            sb.append('\n');

            sb.append('|');
            append(sb, "", NAME_COL_W);
            sb.append('|');
            //append frame totals
            for (int j = 0; j < 10; j++) {
                Frame f = p.game.frames[j];
                if (f.canTotal()) {
                    appendTotal(sb, f.total(), SCORE_COL_W);
                } else {
                    append(sb, "", SCORE_COL_W);
                }
                sb.append('|');
            }

            // game total
            int gameTotal = p.game.gameTotal();
            appendTotal(sb, gameTotal, SCORE_COL_W);
            sb.append("|\n");
            sb.append(DIVIDER);
        }
        out.append(sb.toString());
    }

    static void append(StringBuilder sb, String s, int len) {
        sb.append(s, 0, Math.min(len, s.length()));
        for (int i = 0; i < len - s.length(); i++) {
            sb.append(' ');
        }
    }

    static void appendTotal(StringBuilder sb, int t, int len) {
        sb.append(t);
        int pad = t >= 100 ? len-3 : t >=10 ? len-2 : len -1;
        for (int i = 0; i < pad; i++) {
            sb.append(' ');
        }
    }

    static void appendRoll(StringBuilder sb, int r, int len) {
        if (r < 10) {
            sb.append(r);
        } else {
            sb.append("X");
        }
        for (int i = 0; i < len - 1; i++) {
            sb.append(' ');
        }
    }

    static void append(StringBuilder sb, Frame f, int width) {
        switch (f.rolls) {
            case 0:
                append(sb, "", width);
                break;
            case 1:
                appendRoll(sb, f.firstRoll, width);
                break;
            case 2:
                appendRoll(sb, f.firstRoll, 2);
                if (f.isSpare()) {
                    append(sb, "/", 2);
                } else {
                    appendRoll(sb, f.secondRoll, 2);
                }
                append(sb, "", width - 4);
                break;
            case 3:
                appendRoll(sb, f.firstRoll, 2);
                if (f.isSpare()) {
                    append(sb, "/", 2);
                } else {
                    appendRoll(sb, f.secondRoll, 2);
                }
                appendRoll(sb, f.extraRoll, 2);
                break;
        }
    }





}
