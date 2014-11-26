(require '[clj-webdriver.taxi :as wd])

(wd/set-driver! {:browser :firefox} "http://clojure.org")

(wd/input-text "#q" "multimethod")
(wd/submit "#q")
(-> (wd/find-element {:css ".WikiSearchResult a"})
    wd/click)

