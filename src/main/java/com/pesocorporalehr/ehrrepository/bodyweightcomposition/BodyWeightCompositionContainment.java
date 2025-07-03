package com.pesocorporalehr.ehrrepository.bodyweightcomposition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartyProxy;
import com.pesocorporalehr.ehrrepository.bodyweightcomposition.definition.BodyWeightObservation;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import org.ehrbase.openehr.sdk.generator.commons.aql.containment.Containment;
import org.ehrbase.openehr.sdk.generator.commons.aql.field.AqlFieldImp;
import org.ehrbase.openehr.sdk.generator.commons.aql.field.ListAqlFieldImp;
import org.ehrbase.openehr.sdk.generator.commons.aql.field.ListSelectAqlField;
import org.ehrbase.openehr.sdk.generator.commons.aql.field.SelectAqlField;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Category;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Setting;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Territory;

public class BodyWeightCompositionContainment extends Containment {
  public SelectAqlField<BodyWeightComposition> BODY_WEIGHT_COMPOSITION = new AqlFieldImp<BodyWeightComposition>(BodyWeightComposition.class, "", "BodyWeightComposition", BodyWeightComposition.class, this);

  public SelectAqlField<Category> CATEGORY_DEFINING_CODE = new AqlFieldImp<Category>(BodyWeightComposition.class, "/category|defining_code", "categoryDefiningCode", Category.class, this);

  public ListSelectAqlField<Cluster> EXTENSION = new ListAqlFieldImp<Cluster>(BodyWeightComposition.class, "/context/other_context[at0001]/items[at0002]", "extension", Cluster.class, this);

  public SelectAqlField<TemporalAccessor> START_TIME_VALUE = new AqlFieldImp<TemporalAccessor>(BodyWeightComposition.class, "/context/start_time|value", "startTimeValue", TemporalAccessor.class, this);

  public ListSelectAqlField<Participation> PARTICIPATIONS = new ListAqlFieldImp<Participation>(BodyWeightComposition.class, "/context/participations", "participations", Participation.class, this);

  public SelectAqlField<TemporalAccessor> END_TIME_VALUE = new AqlFieldImp<TemporalAccessor>(BodyWeightComposition.class, "/context/end_time|value", "endTimeValue", TemporalAccessor.class, this);

  public SelectAqlField<String> LOCATION = new AqlFieldImp<String>(BodyWeightComposition.class, "/context/location", "location", String.class, this);

  public SelectAqlField<PartyIdentified> HEALTH_CARE_FACILITY = new AqlFieldImp<PartyIdentified>(BodyWeightComposition.class, "/context/health_care_facility", "healthCareFacility", PartyIdentified.class, this);

  public SelectAqlField<Setting> SETTING_DEFINING_CODE = new AqlFieldImp<Setting>(BodyWeightComposition.class, "/context/setting|defining_code", "settingDefiningCode", Setting.class, this);

  public SelectAqlField<BodyWeightObservation> BODY_WEIGHT = new AqlFieldImp<BodyWeightObservation>(BodyWeightComposition.class, "/content[openEHR-EHR-OBSERVATION.body_weight.v2]", "bodyWeight", BodyWeightObservation.class, this);

  public SelectAqlField<PartyProxy> COMPOSER = new AqlFieldImp<PartyProxy>(BodyWeightComposition.class, "/composer", "composer", PartyProxy.class, this);

  public SelectAqlField<Language> LANGUAGE = new AqlFieldImp<Language>(BodyWeightComposition.class, "/language", "language", Language.class, this);

  public SelectAqlField<FeederAudit> FEEDER_AUDIT = new AqlFieldImp<FeederAudit>(BodyWeightComposition.class, "/feeder_audit", "feederAudit", FeederAudit.class, this);

  public SelectAqlField<Territory> TERRITORY = new AqlFieldImp<Territory>(BodyWeightComposition.class, "/territory", "territory", Territory.class, this);

  private BodyWeightCompositionContainment() {
    super("openEHR-EHR-COMPOSITION.encounter.v1");
  }

  public static BodyWeightCompositionContainment getInstance() {
    return new BodyWeightCompositionContainment();
  }
}
