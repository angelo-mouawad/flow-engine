import { createRouter, createWebHistory } from 'vue-router';
import { useRoleStore } from '../stores/roleStore';
import WelcomeGrid from '../views/Dashboard.vue';
import FlowDefinitionForm from '../views/FlowDefinitionForm.vue';
import FlowDefinitionOverview from '../views/FlowDefinitionOverview.vue';
import FlowInstancesOverview from '../views/FlowInstancesOverview.vue';

const routes = [
  { path: '/', name: 'Home', component: WelcomeGrid },
  { path: '/flow-definitions', name: 'FlowDefinitions', component: FlowDefinitionOverview },
  { path: '/flow-instances', name: 'FlowInstances', component: FlowInstancesOverview },
  { path: '/flow-definitions/new', name: 'FlowDefinitionsNew', component: FlowDefinitionForm, meta: { requiresAdmin: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const roleStore = useRoleStore();

  if (to.meta.requiresAdmin && roleStore.role !== 'Manager') {
    alert('You do not have permission to access this page.');
    return next({ path: '/' });

  }

  next();
});

export default router;
