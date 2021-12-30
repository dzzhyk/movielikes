import {createApp} from "vue";
import App from "./App.vue"
import {createRouter, createWebHistory} from "vue-router";
import HomePage from "@/components/HomePage";
import Login from "@/components/Login";
import Register from "@/components/Register";
import { Form, Field, CellGroup,Button} from 'vant';
import "vant/lib/index.css";
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "HomePage",
      component: HomePage,
      meta:{
        index:0,
      },
    },
    {
      path: "/login",
      name: "Login",
      component: Login,
      meta:{
        index:1,
      },
    },
    {
      path: "/register",
      name: "Register",
      component: Register,
      meta:{
        index:1,
      },
    },
  ],
});

const app = createApp(App);
app.use(Form);
app.use(Field);
app.use(CellGroup);
app.use(Button);
app.use(router);
app.use(ElementPlus);
app.mount("#app");
