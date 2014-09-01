(require '[clojure.data.json :as json])

(def api-key
  "<YOUR KEY HERE>")

(def box-office-uri
  (str 
   "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey="
   api-key
   "&limit=50"))
  
(->> box-office-uri
     slurp
     json/read-json
     :movies
     (mapcat :abridged_cast)
     (map :name)
     frequencies
     (sort-by (comp - second))
     (take 10))
  
[["Shiloh Fernandez" 2] ["Ray Liotta" 2] ["Isla Fisher" 2] ["Bradley Cooper" 2] ["Dwayne \"The Rock\" Johnson" 2] ["Morgan Freeman" 2] ["Michael Shannon" 2] ["Joel Edgerton" 2] ["Susan Sarandon" 2] ["Leonardo DiCaprio" 2]]
