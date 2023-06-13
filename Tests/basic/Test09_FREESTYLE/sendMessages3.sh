#!/bin/bash
# Executed concurrently with sendMessages.sh and sendMessages3.sh
sleep 0.3
echo "NICK Bernie"
echo "USER bw 0 * :Bernard Woolley"
echo "JOIN #alas"
sleep 1.2
echo "PRIVMSG #alas :Yes prime minister."
sleep 0.6
echo "QUIT"
sleep 0.1
