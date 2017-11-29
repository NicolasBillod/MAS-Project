# MAS-Project

Multi-agent system project in Java with JADE framework
Project realized during 1st year of Master in Computer Science in 2017, at UPMC, Paris.

Most of the code was already written, our goal was to implement behaviors for the agents.
Agents had to explore the environment (i.e. undirected graph), communicate, and gather tresors available on a few nodes, depending on their bags' capacity and the type of treasure.
To do so, we chose to explore the world first, exchange maps with communcation, and only then gather the treasures according to who an agent think is the fittest to collect it. To handle the "state" an agent is in (i.e. explorating, communicating,...) we chose to use finite-state machine. You can see what it looks like in page 2 of the report.
