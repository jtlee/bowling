package org.jlee.bowling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BowlingTest {

    @Test
    public void testStrike() {
        Bowling.Player p = new Bowling.Player("TEST");
        Bowling.Frame f1 = p.game.nextFrame();
        rollFrame(p, 10);

        Bowling.Frame f2 = p.game.nextFrame();
        rollFrame(p, 10);

        Bowling.Frame f3 = p.game.nextFrame();
        rollFrame(p, 10);

        assertTrue(f1.canTotal());
        assertEquals(30, f1.total());
        assertEquals(30, p.game.gameTotal());//10+10+10

        Bowling.Frame f4 = p.game.nextFrame();
        rollFrame(p, 5, 5);

        assertTrue(f2.canTotal());
        assertEquals(10 + 10 + 5, f2.total());

        assertTrue(f3.canTotal());
        assertEquals(10 + 5 + 5, f3.total());

        assertFalse(f4.canTotal());

        assertEquals((30 + (10 + 10 + 5) + (10 + 5 + 5)), p.game.gameTotal());
    }

    @Test
    public void testSpare() {
        Bowling.Player p = new Bowling.Player("TEST");

        Bowling.Frame f1 = p.game.nextFrame();
        rollFrame(p, 3, 7);
        assertTrue(p.game.currentFrame().isSpare());
        assertFalse(p.game.currentFrame().isStrike());
        assertFalse(p.game.currentFrame().canRoll());
        assertFalse(p.game.currentFrame().canTotal());

        Bowling.Frame f2 = p.game.nextFrame();
        roll(p, 6);

        assertTrue(f1.canTotal());
        assertEquals(16, f1.total());

        assertFalse(f2.canTotal());
    }

    @MethodSource("testGameArgs")
    @ParameterizedTest
    public void testGame(int[][] frames, int[] frameTotals, int gameTotal) {
        Bowling.Player p = new Bowling.Player("TEST");
        for (int i = 0; i < 10; i++) {
            p.game.nextFrame();
            int[] frameScores = frames[i];
            for (int j = 0; j < frameScores.length; j++) {
                int score = frameScores[j];
                roll(p, score);

                try {
                    Bowling.printScore(System.out, p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            assertFalse(p.game.currentFrame().canRoll());
        }
        for (int i = 0; i < 10; i++) {
            Bowling.Frame f = p.game.frames[i];
            assertEquals(frameTotals[i], f.total());
        }
        assertEquals(gameTotal, p.game.gameTotal());
        assertTrue(p.game.isGameOver());
    }

    public static Object[][] testGameArgs() {
        return new Object[][]{
                {
                        frames("10|10|10|10|10|10|10|10|10|10,10,10"),
                        totals("30|30|30|30|30|30|30|30|30|30"),
                        300
                },
                {
                        frames("5,4|9,1|5,5|6,3|0,1|0,0|10|10|10|5,5,7"),
                        totals("9|15|16|9|1|0|30|25|20|17"),
                        142
                },
                {
                        frames("5,4|9,1|5,5|6,3|0,1|0,0|10|10|7,2|5,4"),
                        totals("9|15|16|9|1|0|27|19|9|9"),
                        114
                },
        };
    }

    static int[][] frames(String s) {
        int[][] frames = new int[10][];
        String[] frameStrs = s.split("\\|");
        assertEquals(10, frameStrs.length);
        for (int i = 0; i < 10; i++) {
            String fs = frameStrs[i];
            String[] scoreStrs = fs.split(",");
            int[] scores = new int[scoreStrs.length];
            for (int j = 0; j < scoreStrs.length; j++) {
                scores[j] = Integer.parseInt(scoreStrs[j]);
            }
            frames[i] = scores;
        }
        return frames;
    }

    static int[] totals(String s) {
        int[] frames = new int[10];
        String[] frameStrs = s.split("\\|");
        assertEquals(10, frameStrs.length);
        for (int i = 0; i < 10; i++) {
            String fs = frameStrs[i];
            frames[i] = Integer.parseInt(fs);
        }
        return frames;
    }

    private void rollFrame(Bowling.Player p, int... scores) {
        Bowling.Frame f = p.game.currentFrame();
        for(Integer i : scores) {
            roll(p, i);
        }
        assertFalse(f.canRoll());
        assertTrue(f.canTotal() || f.isStrike() || f.isSpare());
    }

    private void roll(Bowling.Player p, int score) {
        Bowling.Frame f = p.game.currentFrame();
        assertTrue(f.canRoll());
        assertFalse(f.canTotal());
        f.score(score);
    }

}
