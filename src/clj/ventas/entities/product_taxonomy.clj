(ns ventas.entities.product-taxonomy
  (:require
   [clojure.spec.alpha :as spec]
   [ventas.database :as db]
   [ventas.database.entity :as entity]
   [ventas.entities.i18n :as entities.i18n]
   [ventas.database.generators :as generators]))

(spec/def :product.taxonomy/name ::entities.i18n/ref)

(spec/def :product.taxonomy/keyword ::generators/keyword)

(spec/def :schema.type/product.taxonomy
  (spec/keys :req [:product.taxonomy/name
                   :product.taxonomy/keyword]))

(entity/register-type!
 :product.taxonomy
 {:attributes
  [{:db/ident :product.taxonomy/name
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true}
   {:db/ident :product.taxonomy/keyword
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}]

  :dependencies
  #{:i18n}

  :to-json
  (fn [this]
    (-> this
        (update :product.taxonomy/name (comp entity/to-json entity/find))
        ((:to-json entity/default-type))))})