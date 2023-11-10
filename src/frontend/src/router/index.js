import { createWebHistory, createRouter } from "vue-router";
import Home from "@/views/Home.vue";
import Queries from "@/views/Queries.vue";
import Settings from "@/views/Settings.vue";

const routes = [
  {
    path: "/", // default page
    name: "home",
    component: Home,
  },
  {
    path: "/queries",
    name: "queries",
    component: Queries,
  },
  {
    path: "/settings",
    name: "settings",
    component: Settings,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;