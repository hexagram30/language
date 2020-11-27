#! /usr/bin/env lfescript

;;; --------------------
;;; constants
;;; --------------------

(defun app () 'hxgm30.language)

;;; --------------------
;;; entry point function
;;; --------------------

(defun main
  ((`(,feature . ,_))
   (banner)
   (let ((args (lists:sublist (init:get_plain_arguments) 3 10)))
     (case feature
       (#"name" (dispatch-name-commands args))
       (_ (unknown-feature feature))))))

;;; -----------------
;;; support functions
;;; -----------------

(defun banner ()
  (let* ((banner-file (filename:join `(,(code:priv_dir (app))
                                       "text"
                                       "banner.txt")))
         (`#(ok ,content) (file:read_file banner-file)))
    (io:format "~s~n" `(,content))))

(defun dispatch-name-commands
  (((= `(,subcmd . ,subargs) args))
   ;(io:format "Got args: ~p~n" `(,args))
   (case subcmd
     ("races" (print-list (hxgm30.language.names:races)))
     ("types" (print-list (apply #'hxgm30.language.names:types/1 subargs)))
     ("regen" (hxgm30.language.names:regen))
     (_ (apply #'hxgm30.language.names:gen/2 args)))
   'ok))

(defun unknown-feature (f)
  (io:format "Unknown feature: ~p~n" `(,f)))

(defun print-list (data)
  (lists:map (lambda (x) (io:format "~s~n" `(,x))) data)
  'ok)