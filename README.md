# Bowling Scoring Tool
This application is for scoring bowling
## Install & Run
* Execute `./gradlew check` to compile and run tests
* Execute `./gradlew run -q --console=plain` to run the bowling application

## Rules
* https://en.wikipedia.org/wiki/Ten-pin_bowling#Traditional_scoring
* 10 squares per game
* One or two rolls per square
  * Strike = knock all 10 down on first roll
  * Spare = knock all 10 down in two rolls
  * Open Frame = Two rolls and less then 10 pins
* Scoring
  * Number of pins knocked down + any bonus roll(s)
  * Strike => two bonus rolls = 10 + next 2 rolls
  * Spare => one bonus roll = 10 + next 1 roll
  * Open Frame => no bonus rolls
* Game is over after the 10th frame and all bonus rolls. 

### Example
| Player | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   | 9   | 10  | Total |
|--------|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-------|
| Jake   | X   | 8 / | 5 / | 4 4 |     |     |     |     |     |     |       |
| Score  | 20  | 15  | 14  | 8   |     |     |     |     |     |     | 57    |

* Frame 1: Strike (10 pins) plus next two rolls ( + 8 + 2) = 20 points
* Frame 2: Spare (8 on first, 2 on second) plus next one roll ( +5 ) = 15 points
* Frame 3: Spare (5 on first, 5 on second) plus next one roll ( +4 ) = 14 points
* Frame 4: Open (4 on first, 4 on second), no bonus rolls = 8 points

## Flow
1. Starting from first frame until the ninth the player will take the first roll. 
   1. If a player rolls a strike the turn is over.
   2. If less than ten pins are knocked down, the same player takes a second roll and then the turn is over.
2. On the tenth frame
   1. If the player rolls a strike, they get two bonus rolls.
   2. If the player rolls a spare, they get one bonus roll.
   3. If less than ten pins are knocked down on both rolls, no bonus rolls are rewarded.
3. After each player has a turn, play starts over with the first player on the next frame. 
4. After the 10th frame, the game is over.
5. The player with the most points wins.