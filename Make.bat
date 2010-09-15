del /S/Q Build\*
rmdir /S/Q Build
mkdir Build
mkdir Build\bin
javac -d Build\bin -sourcepath Source\ Source\minesolver\MineSolverApp.java
jar cfe Build\MineSweeperWhiz.jar minesolver.MineSolverApp -C Build\bin .