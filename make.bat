del /S/Q build\*
rmdir /S/Q build
mkdir build
mkdir build\bin
javac -d build\bin -sourcepath source\ source\minesolver\MineSolverApp.java
jar cfe build\MineSweeperWhiz.jar minesolver.MineSolverApp -C build\bin .