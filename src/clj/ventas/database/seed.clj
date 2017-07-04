(ns ventas.database.seed
  (:require [ventas.database :as db]
            [ventas.database.schema :as schema]
            [taoensso.timbre :as timbre :refer (trace debug info warn error)]
            [clojure.test.check.generators :as gen]
            [clojure.spec :as s]))

(defn generate-1
  "Generate one sample of a given spec"
  [spec]
  (gen/generate (s/gen spec)))

(defn generate-n
  "Generates n samples of given spec"
  [spec n]
  (let [generator (s/gen spec)]
    (map (fn [_] (gen/generate generator)) (range n))))

(defn seed-type
  "Seeds the database with n entities of a type"
  [type n]
  (info "Seeding " type)
  (doseq [entity-data (generate-n (keyword "schema.type" (name type)) n)]
    (let [entity-data (db/entity-preseed type entity-data)
          entity (db/entity-create type entity-data)]
      (db/entity-postseed entity))))

(def entity-list [:tax :file :brand :configuration :resource :attribute
                  :attribute-value :category :product :product-variation])

(defn seed
  "Seeds the database with sample data"
  ([]
   (seed false))
  ([reset?]
   (when reset?
     (schema/migrate true))
   (doseq [kw entity-list]
     (doseq [fixture (db/entity-fixtures kw)]
       (db/entity-create kw fixture)))
   (seed-type :tax 10)
   (seed-type :file 10)
   (seed-type :brand 10)
   (seed-type :configuration 20)
   (seed-type :resource 5)
   (seed-type :attribute 10)
   (seed-type :attribute-value 10)
   (seed-type :category 10)
   (seed-type :product 10)
   (seed-type :product-variation 10)))