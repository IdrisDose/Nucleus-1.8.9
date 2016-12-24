/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.dataservices.dataproviders.DataProvider;

public abstract class Service<T> {

    T data;
    private final DataProvider<T> dataProvider;

    Service(DataProvider<T> dataProvider) throws Exception {
        this(dataProvider, true);
    }

    Service(DataProvider<T> dataProvider, boolean loadNow) throws Exception {
        this.dataProvider = dataProvider;
        if (loadNow) {
            data = dataProvider.load();
        }
    }

    public boolean load() {
        try {
            data = dataProvider.load();
            return true;
        } catch (Exception e) {
            Nucleus.getNucleus().getLogger().error(e.getMessage());
            if (Nucleus.getNucleus().isDebugMode()) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public boolean save() {
        try {
            dataProvider.save(data);
            return true;
        } catch (Exception e) {
            Nucleus.getNucleus().getLogger().error(e.getMessage());
            if (Nucleus.getNucleus().isDebugMode()) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public boolean delete() {
        try {
            dataProvider.delete();
            return true;
        } catch (Exception e) {
            Nucleus.getNucleus().getLogger().error(e.getMessage());
            if (Nucleus.getNucleus().isDebugMode()) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
