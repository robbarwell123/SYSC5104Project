CC=g++
CFLAGS=-std=c++17

INCLUDECADMIUM=-I /cadmium/cadmium/include
INCLUDEDESTIMES=-I /cadmium/DESTimes/include
INCLUDEATOMICS=-I /common/atomics
INCLUDES=/common/includes/docker/

# === COUPLED ===
sim.o: sim.cpp
	$(CC) -g -c $(CFLAGS) $(INCLUDECADMIUM) $(INCLUDEDESTIMES) $(INCLUDEATOMICS) sim.cpp -o sim.o

# === DEPLOY ===
sim: sim.o
	$(CC) -g -o sim sim.o $(INCLUDES)StratAirLiftSimMessage.o $(INCLUDES)StratAirliftUtils.o 