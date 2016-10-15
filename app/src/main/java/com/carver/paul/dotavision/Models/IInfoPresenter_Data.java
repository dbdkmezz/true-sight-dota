package com.carver.paul.dotavision.Models;

import java.util.List;

/**
 * Interface for the presenters which will show the information about hero abilities, to be used
 * by the models.
 */
public interface IInfoPresenter_Data {
    void reset();
    void prepareForFreshList();
    void showHeroInfo(List<HeroInfo> enemyHeroes, List<HeroAndAdvantages> advantageData);
}
