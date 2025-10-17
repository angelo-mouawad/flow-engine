import { createRouter, createWebHistory } from 'vue-router';
import WelcomeGrid from '../views/WelcomeGrid.vue';
import FlowDefinitionForm from '../views/FlowDefinitionForm.vue';
import FlowDefinitionOverview from '../views/FlowDefinitionOverview.vue';
import FlowInstancesOverview from '../views/FlowInstancesOverview.vue';

const routes = [
  { path: '/', name: 'Home', component: WelcomeGrid },
  { path: '/flow-definitions', name: 'FlowDefinitions', component: FlowDefinitionOverview},
  { path: '/flow-instances', name: 'FlowInstances', component: FlowInstancesOverview},
  { path: '/flow-definitions/new', name: 'FlowDefinitionsNew', component: FlowDefinitionForm  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
