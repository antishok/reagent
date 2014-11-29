
(ns runtests
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.interop :refer-macros [.' .!]]
            [reagent.debug :refer-macros [dbg println]]
            [demo :as demo]
            [cemerick.cljs.test :as t]
            [testreagent]
            [testcursor]
            [testinterop]
            [testratom]))

(enable-console-print!)

(def test-results (atom nil))

(def test-box {:position 'absolute
               :margin-left -35
               :color :#aaa})

(defn test-output []
  (let [res @test-results]
    [:div {:style test-box}
     (if-not res
       [:div "waiting for tests to run"]
       [:div
        [:p (str "Ran " (:test res) " tests containing "
                 (+ (:pass res) (:fail res) (:error res))
                 " assertions.")]
        [:p (:fail res) " failures, " (:error res) " errors."]])]))

(defn test-output-mini []
  (let [res @test-results]
    (if res
      (if (zero? (+ (:fail res) (:error res)))
        [:div {:style test-box}
         "All tests ok"]
        [test-output])
      [:div {:style test-box} "testing"])))

(defn test-demo []
  [:div
   [test-output]
   [demo/demo]])

(defn ^:export mounttests []
  (reagent/render-component (fn [] [test-demo])
                            (.-body js/document)))

(defn ^:export run-all-tests []
  (println "-----------------------------------------")
  (try
    (reset! test-results (t/run-all-tests))
    (catch js/Object e
      (do
        (println "Testrun failed\n" e "\n" (.-stack e))
        (reset! test-results {:error e}))))
  (println "-----------------------------------------"))

(defn run-tests []
  (if reagent/is-client
    (do
      (reset! test-results nil)
      (js/setTimeout run-all-tests 100))
    (run-all-tests)))
