#!/bin/bash
t=$1
IO=../IO
if ! java -ea Main < ${IO}/input.${t} >& output.${t}
then
    echo tail output.${t}
    exit 1
fi
if ! diff ${IO}/output.${t} output.${t} >& /dev/null
then
    echo diff ${IO}/output.${t} output.${t} 
    exit 1
fi
rm output.${t}
echo good
