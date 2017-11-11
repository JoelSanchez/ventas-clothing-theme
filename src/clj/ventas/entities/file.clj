(ns ventas.entities.file
  (:require [clojure.spec.alpha :as spec]
            [clojure.test.check.generators :as gen]
            [com.gfredericks.test.chuck.generators :as gen']
            [ventas.database :as db]
            [ventas.database.entity :as entity]
            [ventas.config :as config]
            [clojure.java.io :as io]
            [ventas.util :refer [find-files]]
            [ventas.paths :as paths]))

(spec/def :file/extension #{:file.extension/jpg :file.extension/gif :file.extension/png :file.extension/tiff})

(spec/def :schema.type/file
  (spec/keys :req [:file/extension]))

(defn filename [entity]
  (println entity)
  (str (:db/id entity) "." (name (:file/extension entity))))

(entity/register-type!
 :file
 {:attributes
  [{:db/ident :file/extension
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}

   {:db/ident :file.extension/png}
   {:db/ident :file.extension/jpg}
   {:db/ident :file.extension/gif}
   {:db/ident :file.extension/tiff}]

  :filter-seed
  (fn [this]
    (-> this
        (assoc :file/extension :file.extension/jpg)))

  :after-seed
  (fn [this]
    (let [file (rand-nth (find-files (str paths/seeds "/files") (re-pattern ".*?")))
          path (str paths/images "/" (filename this))]
      (io/copy file (io/file path))))

  :filter-json
  (fn [this]
    (let [path (str paths/images "/" (filename this))]
      (-> this
          (assoc :url (paths/path->url path)))))})