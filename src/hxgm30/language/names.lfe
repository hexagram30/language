(defmodule hxgm30.language.names
  (export
   (gen 2)
   (races 0)
   (regen 0)
   (types 1)))

(defun app () 'hxgm30.language)

(defun gen (race type)
  'tbd)

(defun races ()
  (lists:map (clj:comp #'string:titlecase/1 #'filename:basename/1)
             (race-dirs)))

(defun regen ()
  'tbd)

(defun types (race)
  (lists:map (clj:comp #'dash-to-space/1
                       #'string:titlecase/1
                       #'filename:basename/1)
             (type-dirs race)))

;;; -----------------
;;; support functions
;;; -----------------

(defun race-dirs ()
  (filelib:wildcard
   (filename:join `(,(code:priv_dir (app))
                    "corpora"
                    "names"
                    "*"))))

(defun type-dirs (race)
  (filelib:wildcard
   (filename:join `(,(code:priv_dir (app))
                    "corpora"
                    "names"
                    ,(string:lowercase race)
                    "*"))))

(defun race-type-dir (race type)
  (filename:join `(,(code:priv_dir (app))
                   "corpora"
                   "names"
                   ,(string:lowercase race)
                   ,(string:lowercase type))))

(defun dash-to-space (name)
  (re:replace name "[-_]" " " `(global #(return list))))