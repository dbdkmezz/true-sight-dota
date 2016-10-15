package com.carver.paul.dotavision.Ui;

import com.carver.paul.dotavision.Models.IInfoPresenter_Data;

/**
 * Interface for the presenters which will show the information about hero abilities, to be used
 * by fellow presnters.
 */

public interface IInfoPresenter_P {
    void show();
    void reset();
    void hide();
}
