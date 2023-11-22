import { createWebHistory, createRouter } from "vue-router";
import Home from "@/views/Home.vue";
import Queries from "@/views/Queries.vue";
import Settings from "@/views/Settings.vue";
import Client from "@/views/forms/Client.vue";
import ClientForm from "@/views/forms/ClientForm.vue";
import TaskForm from "@/views/forms/TaskForm.vue";

const routes = [
  {
    path: "/", // default page
    name: "home",
    component: Home,
  },
  {
    path: "/client/edit",
    name: "clientNew",
    component: ClientForm,
  },
  {
    path: "/client/edit/:id", // default page
    name: "clientEdit",
    component: ClientForm,
  },
  {
    path: "/client/:id", // default page
    name: "client",
    component: Client,
  },
  {
    path: "/task/:id",
    name: "taskEdit",
    component: TaskForm,
  },
  {
    path: "/task/new/:clientid/",
    name: "taskNew",
    component: TaskForm,
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