(ns ventas.themes.clothing.api
  (:require
   [clojure.spec.alpha :as spec]
   [ventas.database.entity :as entity]
   [ventas.server.api :as api]
   [ventas.database.schema :as schema]))

(spec/def :category/featured boolean?)

(spec/def :product/featured boolean?)

(schema/register-migration!
 ::featured-categories
 [{:db/ident :category/featured
   :db/valueType :db.type/boolean
   :db/cardinality :db.cardinality/one}])

(schema/register-migration!
 ::featured-products
 [{:db/ident :product/featured
   :db/valueType :db.type/boolean
   :db/cardinality :db.cardinality/one}])

(api/register-endpoint!
  ::featured-entities.list
  {:spec {:entity-type #{:category :product}}}
  (fn [{{:keys [entity-type]} :params} {:keys [session]}]
    (->> (entity/query entity-type {:featured true})
         (map (partial api/serialize-with-session session)))))
