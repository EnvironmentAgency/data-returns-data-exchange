package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnPeriodDao;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnPeriod;

import javax.inject.Inject;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReturnPeriodDaoImpl extends AbstractEntityDao<ReturnPeriod> implements ReturnPeriodDao {
    @Inject
    public ReturnPeriodDaoImpl(ApplicationEventPublisher publisher) {
        super(ReturnPeriod.class, publisher);
    }
}