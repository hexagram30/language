%% Copied from:
%% * https://pastebin.com/bGaW7CkK
-module(markov).
-compile(export_all).
-author({jha, abhinav}).

-define(INITPOINT, 50).
-define(ARITY, 3).

start() -> start("corpus.txt").
start(Corpus) -> 
    {ok, Bin} = file:read_file(Corpus),
    Words = string:tokens(binary_to_list(Bin), "\n "),
    Tab = ets:new(words, [bag]),
    form_dictionary(Words, Tab, ?ARITY),
    Start = mrandom(?INITPOINT),
    State0 = lists:sublist(Words, Start, ?ARITY - 1),
    Sentence = spew(Tab, State0, []),
    ets:delete(Tab),
    io:format("~p~n", [string:join(Sentence, " ")]).

spew(Tab, [_|L]=State, Acc)->
    case (length(Acc) >= 20) and (string:rchr(lists:last(L), $.) =:= length(lists:last(L))) of 
        true -> lists:reverse(Acc);
        false -> 
            Objects = ets:lookup(Tab, list_to_tuple(State)),
            {_,Snew} = lists:nth(mrandom(length(Objects)), Objects),
            spew(Tab, L ++ [Snew], [Snew | Acc])
    end.

mrandom(Max) ->
    {A, B, C} = now(),
    random:seed(A,B,C),
    random:uniform(Max).

form_dictionary([], _, _) -> void;
form_dictionary([H|T], Tab, N)->
    ngrams([H|T], Tab, N, []),
    form_dictionary(T, Tab, N).

ngrams([], _, _, _) -> void;
ngrams([H|_], Tab, 1, Acc)-> ets:insert(Tab, {list_to_tuple(lists:reverse(Acc)), H});
ngrams([H|T], Tab, X, Acc)-> ngrams(T, Tab, X-1, [H|Acc]).

