package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.lifecompanion.ui.dashboard.DashboardViewModel
import com.mena97villalobos.lifecompanion.ui.warranty.add.AddEditWarrantyViewModel
import com.mena97villalobos.lifecompanion.ui.warranty.list.WarrantyListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DashboardViewModel(get(), get()) }
    viewModel {
        WarrantyListViewModel(
            getWarranties = get(),
            deleteWarranty = get(),
        )
    }

    viewModel {
        AddEditWarrantyViewModel(
            addWarranty = get(),
            updateWarranty = get(),
            uploadImage = get(),
        )
    }
}
