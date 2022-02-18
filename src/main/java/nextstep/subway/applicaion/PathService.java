package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        Station upStation = stationService.findById(source);
        Station downStation = stationService.findById(target);
        List<Line> lines = lineService.findLines();
        SubwayMap subwayMap = new SubwayDistanceMap(lines);
        Path path = subwayMap.findPath(upStation, downStation);
        Fare fare = pathFare(path);

        return PathResponse.of(path, fare);
    }

    private Fare pathFare(Path path) {
        FarePolicyChain fareDistancePolicy = new FareDistancePolicy();
        FarePolicyChain fareLinePolicy = new FareLinePolicy();
        fareDistancePolicy.nextChain(fareLinePolicy);

        return fareDistancePolicy.calculate(path);
    }

}
