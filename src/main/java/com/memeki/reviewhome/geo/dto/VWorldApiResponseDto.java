package com.memeki.reviewhome.geo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class VWorldApiResponseDto {

    private ResponseDTO response;

    @Override
    public String toString() {
        return "VWorldApiResponseDto{" +
                "response=" + response.toString() +
                '}';
    }

    @Builder
    @JsonCreator
    public VWorldApiResponseDto(@JsonProperty("response") ResponseDTO response) {
        this.response = response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseDTO {
        private ServiceDTO service;
        private String status;
        private RecordDTO record;
        private PageDTO page;
        private ResultDTO result;

        @Builder
        @JsonCreator
        public ResponseDTO(@JsonProperty("service") ServiceDTO service,
                           @JsonProperty("status") String status,
                           @JsonProperty("record") RecordDTO record,
                           @JsonProperty("page") PageDTO page,
                           @JsonProperty("result") ResultDTO result) {
            this.service = service;
            this.status = status;
            this.record = record;
            this.page = page;
            this.result = result;
        }

        @Override
        public String toString() {
            return "ResponseDTO{" +
                    "service=" + service.toString() +
                    ", status='" + status + '\'' +
                    ", record=" + record.toString() +
                    ", page=" + page.toString() +
                    ", result=" + result.toString() +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceDTO {
        private String name;
        private String version;
        private String operation;
        private String time;

        @Builder
        @JsonCreator
        public ServiceDTO(@JsonProperty("name") String name,
                          @JsonProperty("version") String version,
                          @JsonProperty("operation") String operation,
                          @JsonProperty("time") String time) {
            this.name = name;
            this.version = version;
            this.operation = operation;
            this.time = time;
        }

        @Override
        public String toString() {
            return "ServiceDTO{" +
                    "name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", operation='" + operation + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecordDTO {
        private String total;
        private String current;

        @Builder
        @JsonCreator
        public RecordDTO(@JsonProperty("total") String total,
                         @JsonProperty("current") String current) {
            this.total = total;
            this.current = current;
        }

        @Override
        public String toString() {
            return "RecordDTO{" +
                    "total='" + total + '\'' +
                    ", current='" + current + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageDTO {
        private String total;
        private String current;
        private String size;

        @Builder
        @JsonCreator
        public PageDTO(@JsonProperty("total") String total,
                       @JsonProperty("current") String current,
                       @JsonProperty("size") String size) {
            this.total = total;
            this.current = current;
            this.size = size;
        }

        @Override
        public String toString() {
            return "PageDTO{" +
                    "total='" + total + '\'' +
                    ", current='" + current + '\'' +
                    ", size='" + size + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultDTO {
        private String crs;
        private String type;
        private List<ItemDTO> items;

        @Builder
        @JsonCreator
        public ResultDTO(@JsonProperty("crs") String crs,
                         @JsonProperty("type") String type,
                         @JsonProperty("items") List<ItemDTO> items) {
            this.crs = crs;
            this.type = type;
            this.items = items;
        }

        @Override
        public String toString() {
            return "ResultDTO{" +
                    "crs='" + crs + '\'' +
                    ", type='" + type + '\'' +
                    ", items=" + items.toString() +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemDTO {
        private String id;
        private AddressDTO address;
        private PointDTO point;

        @Builder
        @JsonCreator
        public ItemDTO(@JsonProperty("id") String id,
                       @JsonProperty("address") AddressDTO address,
                       @JsonProperty("point") PointDTO point) {
            this.id = id;
            this.address = address;
            this.point = point;
        }

        @Override
        public String toString() {
            return "ItemDTO{" +
                    "id='" + id + '\'' +
                    ", address=" + address.toString() +
                    ", point=" + point.toString() +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressDTO {
        private String zipcode;
        private String category;
        private String road;
        private String parcel;
        private String bldnm;
        private String bldnmdc;

        @Builder
        @JsonCreator
        public AddressDTO(@JsonProperty("zipcode") String zipcode,
                          @JsonProperty("category") String category,
                          @JsonProperty("road") String road,
                          @JsonProperty("parcel") String parcel,
                          @JsonProperty("bldnm") String bldnm,
                          @JsonProperty("bldnmdc") String bldnmdc) {
            this.zipcode = zipcode;
            this.category = category;
            this.road = road;
            this.parcel = parcel;
            this.bldnm = bldnm;
            this.bldnmdc = bldnmdc;
        }

        @Override
        public String toString() {
            return "AddressDTO{" +
                    "zipcode='" + zipcode + '\'' +
                    ", category='" + category + '\'' +
                    ", road='" + road + '\'' +
                    ", parcel='" + parcel + '\'' +
                    ", bldnm='" + bldnm + '\'' +
                    ", bldnmdc='" + bldnmdc + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointDTO {
        private String x;
        private String y;

        @Builder
        @JsonCreator
        public PointDTO(@JsonProperty("x") String x,
                        @JsonProperty("y") String y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "PointDTO{" +
                    "x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    '}';
        }
    }
}
