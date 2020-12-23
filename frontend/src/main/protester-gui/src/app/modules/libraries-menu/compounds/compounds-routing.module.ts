import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {SearchComponent} from "./compound-search/compound-search.component";
import {ViewComponent} from "./compound-view/compound-view.component";
import {CreateComponent} from "./compound-new/compound-new.component";

const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'Compounds'
    },
    children: [
      {
        path: '',
        data: {
          breadcrumb: null
        },
        component: SearchComponent
      },
      {
        path: 'new',
        data: {
          breadcrumb: 'Create'
        },
        component: CreateComponent
      },
      {
        path: ':id',
        data: {
          breadcrumb: null
        },
        children: [
          {
            path: '',
            data: {
              breadcrumb: 'Compound'
            },
            component: ViewComponent
          }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CompoundsRoutingModule { }
