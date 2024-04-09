package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyDTO;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyStructure;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.outputDTO.smells.SmellOutput;

import java.util.ArrayList;
import java.util.List;

public class CompareComputer {

    public static ArchitectureSmellCompareDTO compare(SmellOutput architectureSmellOutputDTO_1, SmellOutput architectureSmellOutputDTO_2) {

        AbstractionWithoutDecouplingCompareDTO abstractionWithoutDecouplingCompareDTO = compare(architectureSmellOutputDTO_1.getAbstractionWithoutDecoupling(), architectureSmellOutputDTO_2.getAbstractionWithoutDecoupling());
        CyclicDependencyCompareDTO cyclicDependencyCompareDTO = compare(architectureSmellOutputDTO_1.getCyclicDependency(), architectureSmellOutputDTO_2.getCyclicDependency());
        CyclicHierarchyCompareDTO cyclicHierarchyCompareDTO = compare(architectureSmellOutputDTO_1.getCyclicHierarchy(), architectureSmellOutputDTO_2.getCyclicHierarchy());
//        FeatureEnvyCompareDTO featureEnvyCompareDTO = compare(architectureSmellOutputDTO_1.getFeatureEnvy(), architectureSmellOutputDTO_2.getFeatureEnvy());
        MultipathHierarchyCompareDTO multipathHierarchyCompareDTO = compare(architectureSmellOutputDTO_1.getMultipathHierarchy(), architectureSmellOutputDTO_2.getMultipathHierarchy());


        int reduced_smell_count = abstractionWithoutDecouplingCompareDTO.getReduced_count_AbstractionWithoutDecoupling()
                + cyclicDependencyCompareDTO.getReduced_count_CyclicDependency()
                + cyclicHierarchyCompareDTO.getReduced_count_CyclicHierarchy()
//                + featureEnvyCompareDTO.getReduced_count_FeatureEnvy()
                + multipathHierarchyCompareDTO.getReduced_count_MultipathHierarchy();

        int added_smell_count = abstractionWithoutDecouplingCompareDTO.getAdded_count_AbstractionWithoutDecoupling()
                + cyclicDependencyCompareDTO.getAdded_count_CyclicDependency()
                + cyclicHierarchyCompareDTO.getAdded_count_CyclicHierarchy()
//                + featureEnvyCompareDTO.getAdded_count_FeatureEnvy()
                + multipathHierarchyCompareDTO.getAdded_count_MultipathHierarchy();

        if (reduced_smell_count == 0 && added_smell_count == 0) {
            return new ArchitectureSmellCompareDTO();
        }

//        if (abstractionWithoutDecouplingCompareDTO.getReduced_count_AbstractionWithoutDecoupling() == 0 && abstractionWithoutDecouplingCompareDTO.getAdded_count_AbstractionWithoutDecoupling() == 0) {
//            abstractionWithoutDecouplingCompareDTO = null;
//        }
//        else if(abstractionWithoutDecouplingCompareDTO.getReduced_count_AbstractionWithoutDecoupling() == 0){
//            abstractionWithoutDecouplingCompareDTO.setReduced_count_AbstractionWithoutDecoupling(null);
//            abstractionWithoutDecouplingCompareDTO.setReduced_abstractionWithoutDecouplingStructureList(null);
//
//        }
//        else if(abstractionWithoutDecouplingCompareDTO.getAdded_count_AbstractionWithoutDecoupling() == 0){
//            abstractionWithoutDecouplingCompareDTO.setAdded_count_AbstractionWithoutDecoupling(null);
//            abstractionWithoutDecouplingCompareDTO.setAdded_abstractionWithoutDecouplingStructureList(null);
//        }
//
//
//
//        if (cyclicDependencyCompareDTO.getReduced_count_CyclicDependency() == 0 && cyclicDependencyCompareDTO.getAdded_count_CyclicDependency() == 0) {
//            cyclicDependencyCompareDTO = null;
//        }
//        else if(cyclicDependencyCompareDTO.getReduced_count_CyclicDependency() == 0){
//            cyclicDependencyCompareDTO.setReduced_count_CyclicDependency(null);
//            cyclicDependencyCompareDTO.setReduced_cyclicDependencyStructureList(null);
//
//        }
//        else if(cyclicDependencyCompareDTO.getAdded_count_CyclicDependency() == 0){
//            cyclicDependencyCompareDTO.setAdded_count_CyclicDependency(null);
//            cyclicDependencyCompareDTO.setAdded_cyclicDependencyStructureList(null);
//        }
//
//
//        if (cyclicHierarchyCompareDTO.getReduced_count_CyclicHierarchy() == 0 && cyclicHierarchyCompareDTO.getAdded_count_CyclicHierarchy() == 0) {
//            cyclicHierarchyCompareDTO = null;
//        }
//        else if(cyclicHierarchyCompareDTO.getReduced_count_CyclicHierarchy() == 0){
//            cyclicHierarchyCompareDTO.setReduced_count_CyclicHierarchy(null);
//            cyclicHierarchyCompareDTO.setReduced_cyclicHierarchyStructureList(null);
//
//        }
//        else if(cyclicHierarchyCompareDTO.getAdded_count_CyclicHierarchy() == 0){
//            cyclicHierarchyCompareDTO.setAdded_count_CyclicHierarchy(null);
//            cyclicHierarchyCompareDTO.setAdded_cyclicHierarchyStructureList(null);
//        }
//
//
//        if (featureEnvyCompareDTO.getReduced_count_FeatureEnvy() == 0 && featureEnvyCompareDTO.getAdded_count_FeatureEnvy() == 0) {
//            featureEnvyCompareDTO = null;
//        }
//        else if(featureEnvyCompareDTO.getReduced_count_FeatureEnvy() == 0){
//            featureEnvyCompareDTO.setReduced_count_FeatureEnvy(null);
//            featureEnvyCompareDTO.setReduced_featureEnvyStructureList(null);
//
//        }
//        else if(featureEnvyCompareDTO.getAdded_count_FeatureEnvy() == 0){
//            featureEnvyCompareDTO.setAdded_count_FeatureEnvy(null);
//            featureEnvyCompareDTO.setAdded_featureEnvyStructureList(null);
//        }
//
//        if (multipathHierarchyCompareDTO.getReduced_count_MultipathHierarchy() == 0 && multipathHierarchyCompareDTO.getAdded_count_MultipathHierarchy() == 0) {
//            multipathHierarchyCompareDTO = null;
//        }
//        else if(multipathHierarchyCompareDTO.getReduced_count_MultipathHierarchy() == 0){
//            multipathHierarchyCompareDTO.setReduced_count_MultipathHierarchy(null);
//            multipathHierarchyCompareDTO.setReduced_multipathHierarchyStructureList(null);
//
//        }
//        else if(multipathHierarchyCompareDTO.getAdded_count_MultipathHierarchy() == 0){
//            multipathHierarchyCompareDTO.setAdded_count_MultipathHierarchy(null);
//            multipathHierarchyCompareDTO.setAdded_multipathHierarchyStructureList(null);
//        }


        return new ArchitectureSmellCompareDTO(reduced_smell_count, added_smell_count, abstractionWithoutDecouplingCompareDTO, cyclicDependencyCompareDTO, cyclicHierarchyCompareDTO, multipathHierarchyCompareDTO);
    }

    public static AbstractionWithoutDecouplingCompareDTO compare(AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO_1, AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO_2) {
        System.out.println("test");

        AbstractionWithoutDecouplingCompareDTO abstractionWithoutDecouplingCompareDTO = new AbstractionWithoutDecouplingCompareDTO();
        List<AbstractionWithoutDecouplingStructure> added_abstractionWithoutDecouplingStructures = new ArrayList<>(abstractionWithoutDecouplingDTO_2.getInstances());
        List<AbstractionWithoutDecouplingStructure> reduced_abstractionWithoutDecouplingStructures = new ArrayList<>(abstractionWithoutDecouplingDTO_1.getInstances());
        if (!added_abstractionWithoutDecouplingStructures.equals(reduced_abstractionWithoutDecouplingStructures)) {
            reduced_abstractionWithoutDecouplingStructures.removeAll(abstractionWithoutDecouplingDTO_2.getInstances());
            abstractionWithoutDecouplingCompareDTO.setReduced_AbstractionWithoutDecouplingStructures(reduced_abstractionWithoutDecouplingStructures);
            added_abstractionWithoutDecouplingStructures.removeAll(abstractionWithoutDecouplingDTO_1.getInstances());
            abstractionWithoutDecouplingCompareDTO.setAdded_AbstractionWithoutDecouplingStructures(added_abstractionWithoutDecouplingStructures);
        }

        List<DiffAbstractionWithoutDecouplingStructure> diffAbstractionWithoutDecouplingStructures = new ArrayList<>();
        List<DiffAbstractionWithoutDecouplingStructure> sameAbstractionWithoutDecouplingStructures = new ArrayList<>();
        List<DiffAbstractionWithoutDecouplingStructure> diffAndSameAbstractionWithoutDecouplingStructures = new ArrayList<>();

        if (!abstractionWithoutDecouplingDTO_2.getInstances().equals(abstractionWithoutDecouplingDTO_1.getInstances())) {
            for (int i = 0; i < abstractionWithoutDecouplingDTO_1.getInstances().size(); i++) {
                AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure1 = abstractionWithoutDecouplingDTO_1.getInstances().get(i);
                for (int j = 0; j < abstractionWithoutDecouplingDTO_2.getInstances().size(); j++) {
                    AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure2 = abstractionWithoutDecouplingDTO_2.getInstances().get(j);

                    if (abstractionWithoutDecouplingStructure1.equals(abstractionWithoutDecouplingStructure2)) {
                        // super sub client 相同 details可能不同 接着比较details？
                        List<EntityDependencyRelationDetail> details_client2super_1 = abstractionWithoutDecouplingStructure1.getDetails().getClientClass2superType();
                        List<EntityDependencyRelationDetail> details_client2super_2 = abstractionWithoutDecouplingStructure2.getDetails().getClientClass2superType();

                        List<EntityDependencyRelationDetail> expanded_abstractionWithoutDecoupling_details_client2super = new ArrayList<>(details_client2super_2);
                        expanded_abstractionWithoutDecoupling_details_client2super.removeAll(details_client2super_1);

                        List<EntityDependencyRelationDetail> shrunken_abstractionWithoutDecoupling_details_client2super = new ArrayList<>(details_client2super_1);
                        shrunken_abstractionWithoutDecoupling_details_client2super.removeAll(details_client2super_2);

                        List<EntityDependencyRelationDetail> details_client2sub_1 = abstractionWithoutDecouplingStructure1.getDetails().getClientClass2subType();
                        List<EntityDependencyRelationDetail> details_client2sub_2 = abstractionWithoutDecouplingStructure2.getDetails().getClientClass2subType();

                        List<EntityDependencyRelationDetail> expanded_abstractionWithoutDecoupling_details_client2sub = new ArrayList<>(details_client2sub_2);
                        expanded_abstractionWithoutDecoupling_details_client2sub.removeAll(details_client2sub_1);

                        List<EntityDependencyRelationDetail> shrunken_abstractionWithoutDecoupling_details_client2sub = new ArrayList<>(details_client2sub_1);
                        shrunken_abstractionWithoutDecoupling_details_client2sub.removeAll(details_client2sub_2);


                        List<EntityDependencyRelationDetail> same_abstractionWithoutDecoupling_details_client2sub = new ArrayList<>(details_client2sub_1);
                        same_abstractionWithoutDecoupling_details_client2sub.retainAll(details_client2sub_2);

                        List<EntityDependencyRelationDetail> same_abstractionWithoutDecoupling_details_client2super = new ArrayList<>(details_client2super_1);
                        same_abstractionWithoutDecoupling_details_client2super.retainAll(details_client2super_2);


                        ExpandOrShrunkenAbstractionWithoutDecouplingStructureDetails expandOrShrunkenAbstractionWithoutDecouplingStructureDetails;
                        if (expanded_abstractionWithoutDecoupling_details_client2super.size() == 0 && shrunken_abstractionWithoutDecoupling_details_client2super.size() == 0
                                && expanded_abstractionWithoutDecoupling_details_client2sub.size() == 0 && shrunken_abstractionWithoutDecoupling_details_client2sub.size() == 0) {
                            expandOrShrunkenAbstractionWithoutDecouplingStructureDetails = null;
                        } else {
                            expandOrShrunkenAbstractionWithoutDecouplingStructureDetails = new ExpandOrShrunkenAbstractionWithoutDecouplingStructureDetails();
                            if (expanded_abstractionWithoutDecoupling_details_client2super.size() != 0) {
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setCount_expandedClientClass2superType(expanded_abstractionWithoutDecoupling_details_client2super.size());
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setExpandedClientClass2superType(expanded_abstractionWithoutDecoupling_details_client2super);
                            }
                            if (shrunken_abstractionWithoutDecoupling_details_client2super.size() != 0) {
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setCount_shrunkenClientClass2superType(shrunken_abstractionWithoutDecoupling_details_client2super.size());
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setShrunkenClientClass2superType(shrunken_abstractionWithoutDecoupling_details_client2super);
                            }

                            if (expanded_abstractionWithoutDecoupling_details_client2sub.size() != 0) {
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setCount_expandedClientClass2subType(expanded_abstractionWithoutDecoupling_details_client2sub.size());
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setExpandedClientClass2subType(expanded_abstractionWithoutDecoupling_details_client2sub);
                            }
                            if (shrunken_abstractionWithoutDecoupling_details_client2sub.size() != 0) {
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setCount_shrunkenClientClass2subType(shrunken_abstractionWithoutDecoupling_details_client2sub.size());
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails.setShrunkenClientClass2subType(shrunken_abstractionWithoutDecoupling_details_client2sub);
                            }
                        }

                        SameAbstractionWithoutDecouplingStructureDetails sameAbstractionWithoutDecouplingStructureDetails;
                        if (same_abstractionWithoutDecoupling_details_client2super.size() == 0 && same_abstractionWithoutDecoupling_details_client2sub.size() == 0) {
                            sameAbstractionWithoutDecouplingStructureDetails = null;
                        } else {
                            sameAbstractionWithoutDecouplingStructureDetails = new SameAbstractionWithoutDecouplingStructureDetails(
                                    same_abstractionWithoutDecoupling_details_client2super, same_abstractionWithoutDecoupling_details_client2sub);
                        }


                        DiffAbstractionWithoutDecouplingStructure diffAbstractionWithoutDecouplingStructure = new DiffAbstractionWithoutDecouplingStructure(
                                abstractionWithoutDecouplingStructure1.getId(), abstractionWithoutDecouplingStructure2.getId(),
                                abstractionWithoutDecouplingStructure1.getSuperType(), abstractionWithoutDecouplingStructure1.getClientClass(), abstractionWithoutDecouplingStructure1.getSubType(),
                                expandOrShrunkenAbstractionWithoutDecouplingStructureDetails, sameAbstractionWithoutDecouplingStructureDetails);

                        if (sameAbstractionWithoutDecouplingStructureDetails == null && expandOrShrunkenAbstractionWithoutDecouplingStructureDetails != null) {
                            // 说明只存在演化
                            diffAbstractionWithoutDecouplingStructures.add(diffAbstractionWithoutDecouplingStructure);
                        } else if (sameAbstractionWithoutDecouplingStructureDetails != null && expandOrShrunkenAbstractionWithoutDecouplingStructureDetails == null) {
                            // 说明是完全相同
                            sameAbstractionWithoutDecouplingStructures.add(diffAbstractionWithoutDecouplingStructure);
                        } else if (sameAbstractionWithoutDecouplingStructureDetails != null) {
                            diffAndSameAbstractionWithoutDecouplingStructures.add(diffAbstractionWithoutDecouplingStructure);
                        }
                        break;
                    }
                }
            }
        }

        abstractionWithoutDecouplingCompareDTO.setReduced_count_AbstractionWithoutDecoupling(reduced_abstractionWithoutDecouplingStructures.size());
        abstractionWithoutDecouplingCompareDTO.setAdded_count_AbstractionWithoutDecoupling(added_abstractionWithoutDecouplingStructures.size());
        abstractionWithoutDecouplingCompareDTO.setDiff_count_AbstractionWithoutDecoupling(diffAbstractionWithoutDecouplingStructures.size());
        abstractionWithoutDecouplingCompareDTO.setSame_count_AbstractionWithoutDecoupling(sameAbstractionWithoutDecouplingStructures.size());
        abstractionWithoutDecouplingCompareDTO.setDiffAndSame_count_AbstractionWithoutDecoupling(diffAndSameAbstractionWithoutDecouplingStructures.size());

        abstractionWithoutDecouplingCompareDTO.setDiffAndSame_AbstractionWithoutDecouplingStructures(diffAndSameAbstractionWithoutDecouplingStructures);
        abstractionWithoutDecouplingCompareDTO.setSame_AbstractionWithoutDecouplingStructures(sameAbstractionWithoutDecouplingStructures);
        abstractionWithoutDecouplingCompareDTO.setDiff_AbstractionWithoutDecouplingStructures(diffAbstractionWithoutDecouplingStructures);


        return abstractionWithoutDecouplingCompareDTO;
    }


    public static CyclicDependencyCompareDTO compare(CyclicDependencyDTO cyclicDependencyDTO_1, CyclicDependencyDTO cyclicDependencyDTO_2) {
        CyclicDependencyCompareDTO cyclicDependencyCompareDTO = new CyclicDependencyCompareDTO();

        List<CyclicDependencyStructure> added_cyclicDependencyStructure = new ArrayList<>(cyclicDependencyDTO_2.getInstances());
        List<CyclicDependencyStructure> reduced_cyclicDependencyStructure = new ArrayList<>(cyclicDependencyDTO_1.getInstances());

        List<CyclicDependencyStructure> cyclicDependencyStructures_1 = cyclicDependencyDTO_1.getInstances();
        List<CyclicDependencyStructure> cyclicDependencyStructures_2 = cyclicDependencyDTO_2.getInstances();

        if (!added_cyclicDependencyStructure.equals(reduced_cyclicDependencyStructure)) {
            reduced_cyclicDependencyStructure.removeAll(cyclicDependencyStructures_2);
            cyclicDependencyCompareDTO.setReduced_count_CyclicDependency(reduced_cyclicDependencyStructure.size());
            cyclicDependencyCompareDTO.setReduced_cyclicDependencyStructureList(reduced_cyclicDependencyStructure);

            added_cyclicDependencyStructure.removeAll(cyclicDependencyStructures_1);
            cyclicDependencyCompareDTO.setAdded_count_CyclicDependency(added_cyclicDependencyStructure.size());
            cyclicDependencyCompareDTO.setAdded_cyclicDependencyStructureList(added_cyclicDependencyStructure);

        }

        return cyclicDependencyCompareDTO;
    }

    public static CyclicHierarchyCompareDTO compare(CyclicHierarchyDTO cyclicHierarchyDTO_1, CyclicHierarchyDTO cyclicHierarchyDTO_2) {
        CyclicHierarchyCompareDTO cyclicHierarchyCompareDTO = new CyclicHierarchyCompareDTO();

        List<CyclicHierarchyStructure> added_cyclicHierarchyStructure = new ArrayList<>(cyclicHierarchyDTO_2.getInstances());
        List<CyclicHierarchyStructure> reduced_cyclicHierarchyStructure = new ArrayList<>(cyclicHierarchyDTO_1.getInstances());

        List<CyclicHierarchyStructure> cyclicHierarchyStructures_1 = cyclicHierarchyDTO_1.getInstances();
        List<CyclicHierarchyStructure> cyclicHierarchyStructures_2 = cyclicHierarchyDTO_2.getInstances();

        if (!added_cyclicHierarchyStructure.equals(reduced_cyclicHierarchyStructure)) {
            reduced_cyclicHierarchyStructure.removeAll(cyclicHierarchyStructures_2);
            cyclicHierarchyCompareDTO.setReduced_count_CyclicHierarchy(reduced_cyclicHierarchyStructure.size());
            cyclicHierarchyCompareDTO.setReduced_cyclicHierarchyStructureList(reduced_cyclicHierarchyStructure);

            added_cyclicHierarchyStructure.removeAll(cyclicHierarchyStructures_1);
            cyclicHierarchyCompareDTO.setAdded_count_CyclicHierarchy(added_cyclicHierarchyStructure.size());
            cyclicHierarchyCompareDTO.setAdded_cyclicHierarchyStructureList(added_cyclicHierarchyStructure);

        }

        return cyclicHierarchyCompareDTO;
    }

    public static MultipathHierarchyCompareDTO compare(MultipathHierarchyDTO multipathHierarchyDTO_1, MultipathHierarchyDTO multipathHierarchyDTO_2) {
        MultipathHierarchyCompareDTO multipathHierarchyCompareDTO = new MultipathHierarchyCompareDTO();
        List<MultipathHierarchyStructure> added_multipathHierarchyStructure = new ArrayList<>(multipathHierarchyDTO_2.getInstances());
        List<MultipathHierarchyStructure> reduced_multipathHierarchyStructure = new ArrayList<>(multipathHierarchyDTO_1.getInstances());

        List<MultipathHierarchyStructure> multipathHierarchyStructures_1 = multipathHierarchyDTO_1.getInstances();
        List<MultipathHierarchyStructure> multipathHierarchyStructures_2 = multipathHierarchyDTO_2.getInstances();

        if (!reduced_multipathHierarchyStructure.equals(multipathHierarchyStructures_2)) {
            reduced_multipathHierarchyStructure.removeAll(multipathHierarchyStructures_2);
            multipathHierarchyCompareDTO.setReduced_count_MultipathHierarchy(reduced_multipathHierarchyStructure.size());
            multipathHierarchyCompareDTO.setReduced_multipathHierarchyStructureList(reduced_multipathHierarchyStructure);

            added_multipathHierarchyStructure.removeAll(multipathHierarchyStructures_1);
            multipathHierarchyCompareDTO.setAdded_count_MultipathHierarchy(added_multipathHierarchyStructure.size());
            multipathHierarchyCompareDTO.setAdded_multipathHierarchyStructureList(added_multipathHierarchyStructure);
        }
        return multipathHierarchyCompareDTO;
    }

    public static FeatureEnvyCompareDTO compare(FeatureEnvyDTO featureEnvyDTO_1, FeatureEnvyDTO featureEnvyDTO_2) {
        FeatureEnvyCompareDTO featureEnvyCompareDTO = new FeatureEnvyCompareDTO();
        List<FeatureEnvyStructure> added_featureEnvyStructure = new ArrayList<>(featureEnvyDTO_2.getInstances());
        List<FeatureEnvyStructure> reduced_featureEnvyStructure = new ArrayList<>(featureEnvyDTO_1.getInstances());

        List<FeatureEnvyStructure> featureEnvyStructures_1 = featureEnvyDTO_1.getInstances();
        List<FeatureEnvyStructure> featureEnvyStructures_2 = featureEnvyDTO_2.getInstances();

        if (!reduced_featureEnvyStructure.equals(featureEnvyStructures_2)) {
            reduced_featureEnvyStructure.removeAll(featureEnvyStructures_2);
            featureEnvyCompareDTO.setReduced_count_FeatureEnvy(reduced_featureEnvyStructure.size());
            featureEnvyCompareDTO.setReduced_featureEnvyStructureList(reduced_featureEnvyStructure);

            added_featureEnvyStructure.removeAll(featureEnvyStructures_1);
            featureEnvyCompareDTO.setAdded_count_FeatureEnvy(added_featureEnvyStructure.size());
            featureEnvyCompareDTO.setAdded_featureEnvyStructureList(added_featureEnvyStructure);
        }
        return featureEnvyCompareDTO;
    }
}
