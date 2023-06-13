#!/bin/bash
# Executed concurrently with sendMessages2.sh and sendMessages3.sh
echo "NICK Jim"
echo "USER jgh 0 * :James George Hacker"
echo "JOIN #alas"
echo "LIST"
sleep 1.0
echo "NAMES #alas"
echo "PRIVMSG #alas :Humphrey and Bernard, come in please!"
sleep 1.5
echo "QUIT"
sleep 0.1
