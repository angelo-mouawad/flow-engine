import { createRouter, createWebHistory } from 'vue-router';
import { useRoleStore } from '../stores/roleStore';
import WelcomeGrid from '../views/Dashboard.vue';
import FlowDefinitionForm from '../components/flows/FlowDefinitionForm.vue';
import FlowDefinitionOverview from '../views/FlowDefinitionOverview.vue';
import FlowDefinitionDetails from '../views/FlowDefinitionDetails.vue';
import FlowInstancesOverview from '../views/FlowInstancesOverview.vue';
import FlowInstanceDetails from '../views/FlowInstanceDetails.vue';
import ManageRequests from '../views/ManageRequestsOverview.vue';
import RequestDetails from '../views/RequestDetails.vue';

const routes = [
  { path: '/', name: 'Home', component: WelcomeGrid },
  { path: '/flow-definitions', name: 'FlowDefinitions', component: FlowDefinitionOverview },
  { path: '/flow-definitions/:id', name: 'FlowDefinitionDetails', component: FlowDefinitionDetails },
  { path: '/flow-instances', name: 'FlowInstances', component: FlowInstancesOverview },
  { path: '/flow-instances/:id', name: 'FlowInstanceDetails', component: FlowInstanceDetails },
  { path: '/flow-definitions/new', name: 'FlowDefinitionsNew', component: FlowDefinitionForm, meta: { requiresAdmin: true } },
  { path: '/manage-requests', name: 'ManageRequests', component: ManageRequests },
  { path: '/manage-requests/:id', name: 'RequestDetails', component: RequestDetails },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const roleStore = useRoleStore();
  console.log(from);

  if (to.meta.requiresAdmin && roleStore.role !== 'Manager') {
    alert('You do not have permission to access this page.');
    return next({ path: '/' });

  }

  next();
});

export default router;
