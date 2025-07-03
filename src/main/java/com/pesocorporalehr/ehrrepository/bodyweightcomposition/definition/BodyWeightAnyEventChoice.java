package com.pesocorporalehr.ehrrepository.bodyweightcomposition.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import java.lang.Double;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;

@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-07-03T15:40:56.868131200+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
public interface BodyWeightAnyEventChoice {
  Double getWeightMagnitude();

  void setWeightMagnitude(Double weightMagnitude);

  TemporalAccessor getTimeValue();

  void setTimeValue(TemporalAccessor timeValue);

  NullFlavour getWeightNullFlavourDefiningCode();

  void setWeightNullFlavourDefiningCode(NullFlavour weightNullFlavourDefiningCode);

  NullFlavour getStateOfDressNullFlavourDefiningCode();

  void setStateOfDressNullFlavourDefiningCode(NullFlavour stateOfDressNullFlavourDefiningCode);

  StateOfDressDefiningCode getStateOfDressDefiningCode();

  void setStateOfDressDefiningCode(StateOfDressDefiningCode stateOfDressDefiningCode);

  FeederAudit getFeederAudit();

  void setFeederAudit(FeederAudit feederAudit);

  String getWeightUnits();

  void setWeightUnits(String weightUnits);
}
