#/bin/bash

CMD=$1

DATA=resources/json/names.json

IFS=\ 

if [[ "$CMD" == "races" ]]; then
  echo `cat $DATA | jq '.name[].name' | tr -d \"`
  echo
elif [[ "$CMD" == "types" ]]; then
  RACE=$2
  echo `cat $DATA | jq ".name[] | select(.name==\"$RACE\") | .tables[].option" | tr -d \"`
  echo
else
  RACE=$1
  NAME_TYPE=$2
  echo `cat $DATA | jq ".name[] | select(.name==\"$RACE\") | .tables[] | select(.option==\"$NAME_TYPE\") | .table[].result" | tr -d \"`
  echo
fi
