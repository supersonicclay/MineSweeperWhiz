del /S/Q Build\*
rmdir /S/Q Build
mkdir Build
mkdir Build\bin
javac -d Build\bin -sourcepath Source\ Source\minesweeperwhiz\MineSweeperWhizApp.java
jar cfe Build\MineSweeperWhiz.jar minesweeperwhiz.MineSweeperWhizApp -C Build\bin .