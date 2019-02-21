(ns ventas.themes.clothing.api
  (:require
   [clojure.spec.alpha :as spec]
   [ventas.database.entity :as entity]
   [ventas.server.api :as api]
   [ventas.database.schema :as schema]
   [ventas.database :as db]))

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

(spec/def ::featured-entity-type #{:category :product})

(api/register-endpoint!
  ::featured-entities.list
  {:spec {:entity-type ::featured-entity-type}}
  (fn [{{:keys [entity-type]} :params} {:keys [session]}]
    (->> (entity/query entity-type {:featured true})
         (map (partial api/serialize-with-session session)))))

(api/register-endpoint!
  ::config
  (fn [_ {:keys [session]}]
    (some->> (entity/query-one :clothing-theme)
             (api/serialize-with-session session))))

(api/register-endpoint!
  ::sibling-products.list
  (fn [{{:keys [id]} :params} {:keys [session]}]
    (let [id (api/resolve-ref id :product/keyword)
          {:product/keys [categories]} (entity/find id)]
      (->> (db/nice-query {:find '[?id]
                           :in {'?categories (set categories)
                                '?source-id id}
                           :where '[[?id :product/categories ?category]
                                    (not [(= ?id ?source-id)])
                                    [(contains? ?categories ?category)]]})
           (map (comp entity/find :id))
           (map (partial api/serialize-with-session session))))))
