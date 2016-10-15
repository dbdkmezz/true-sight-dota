package com.carver.paul.dotavision.Models;

import java.util.List;

/**
 * Interface for the presenters which will show the information about hero abilities, to be used
 * by the models.
 */
public interface IInfoPresenter_Data {
    void reset();

    // This gets sent immediately after the presenter is sent a completely new set of heroes, so
    // it can act accordingly
    void prepareForFreshList();

    // When hero info is loaded these two functions will always be called in turn. The first as
    // soon as we have identified the enemy heroes, the second once we have loaded the advantage
    // data for those heroes.
    void setEnemyHeroes(List<HeroInfo> enemyHeroes);
    void setAdvantageData(List<HeroAndAdvantages> advantageData);
}
