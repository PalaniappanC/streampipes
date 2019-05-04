import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {CommonModule} from '@angular/common';

import {AssetComponent} from './asset.component';

import {CustomMaterialModule} from '../CustomMaterial/custom-material.module';
import {NguiDatetimePickerModule} from '@ngui/datetime-picker';
import {DatalakeRestService} from '../core-services/datalake/datalake-rest.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkTableModule} from '@angular/cdk/table';
import {MatProgressSpinnerModule, MatSnackBarModule} from '@angular/material';
import {CoreUiModule} from '../core-iu/core-ui.module';


@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        NguiDatetimePickerModule,
        ReactiveFormsModule,
        FormsModule,
        CdkTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        CoreUiModule,
    ],
    declarations: [
        AssetComponent,
    ],
    providers: [
        DatalakeRestService
    ],
    entryComponents: [
        AssetComponent,
    ],
    exports: [
    ]
})
export class AssetModule {
}