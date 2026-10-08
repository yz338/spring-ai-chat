package com.example.mcpserver.tool;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mcpserver.enums.ListSortEnum;
import com.example.mcpserver.enums.PriceCompareEnum;
import com.example.mcpserver.mapper.ProductMapper;
import com.example.mcpserver.pojo.Product;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductTool {
    private final ProductMapper productMapper;
    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @ToolParam(description = "商品名称")
        private String productName;
        @ToolParam(description = "商品品牌")
        private String brand;
        @ToolParam(description = "商品简介")
        private String description;
        @ToolParam(description = "商品价格")
        private Integer price;
        @ToolParam(description = "商品库存数量")
        private Integer stock;
        @ToolParam(description = "商品状态，上架的值为1，下架的值为0,预售状态的值为2")
        private Integer status;
    }
    @Tool(description = "根据商品id删除商品记录")
    @Transactional
    public String deleteProduct(String productId) {
        log.info("============调用mcp工具:deleteProduct");
        log.info("============<UNK>mcp<UNK>:productId{}:",productId);
        Wrapper<Product> queryWrapper=new QueryWrapper<Product>()
                .eq("product_id",productId);
        productMapper.delete(queryWrapper);
        log.info("============调用mcp工具:deleteProduct END");
        return "商品信息删除成功";
    }

    @Transactional
    @Tool(description = "创建/新增商品信息记录")
    public String createNewProduct(CreateProductRequest createProductRequest) {
        log.info("============调用mcp工具:createNewProduct");
        log.info("============<UNK>mcp<UNK>:createProductRequest{}:",createProductRequest);

        Product product = new Product();
        BeanUtils.copyProperties(createProductRequest,product);

        product.setProductId(RandomStringUtils.randomNumeric(12));
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);

        log.info("============调用mcp工具:createNewProduct  END");
        return "商品信息创建成功";
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryProduct {
        @ToolParam(description = "商品编号", required = false)
        private String productId;
        @ToolParam(description = "商品名称", required = false)
        private String productName;
        @ToolParam(description = "商品品牌", required = false)
        private String brand;

        @ToolParam(description = "商品价格", required = false)
        private Integer price;
        @ToolParam(description = "商品库存数量", required = false)
        private Integer stock;
        @ToolParam(description = "商品状态，上架的值为1，下架的值为0,预售状态的值为2", required = false)
        private Integer status;
        @ToolParam(description = "查询商品列的排序", required = false)
        private ListSortEnum sortEnum;
        @ToolParam(description = "比较价格的大下", required = false)
        private PriceCompareEnum priceCompareEnum;
    }
    @Transactional
    @Tool(description = "商品排序")
    public ListSortEnum getSortProduct(String sort) {
        log.info("==========调用mcp工具:getSortProduct");
        log.info("==========参数mcp参数:sort{}:", sort);
        if (sort.equalsIgnoreCase(ListSortEnum.ASC.value)) {
            return ListSortEnum.ASC;
        }else {
            return ListSortEnum.DESC;
        }
    }
    @Transactional
    @Tool(description = "把商品的价格比较（大于，小于，高于，低于，不高于，不低于，等于，大于等于，小于等于转换为对应的枚举）")
    public PriceCompareEnum getComparePriceProduce(String priceCompare) {
        log.info("==========调用mcp工具:getSortProduct");
        log.info("==========参数mcp参数:sort{}:", priceCompare);
        if (priceCompare.equalsIgnoreCase(PriceCompareEnum.GREATER_THAN.value)) {
            return PriceCompareEnum.GREATER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LESS_THAN.value)) {
            return PriceCompareEnum.LESS_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.GREATER_THAN_OR_EQUAL_TO.value)) {
            return PriceCompareEnum.GREATER_THAN_OR_EQUAL_TO;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LESS_THAN_OR_EQUAL_TO.value)) {
            return PriceCompareEnum.LESS_THAN_OR_EQUAL_TO;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.HIGHER_THAN.value)) {
            return PriceCompareEnum.HIGHER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LOWER_THAN.value)) {
            return PriceCompareEnum.LOWER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.NOT_HIGHER_THAN.value)) {
            return PriceCompareEnum.NOT_HIGHER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.NOT_LOWER_THAN.value)) {
            return PriceCompareEnum.NOT_LOWER_THAN;
        } else {
            return PriceCompareEnum.EQUAL_TO;
        }
    }
    @Transactional
    @Tool(description = "根据条件查询商品")
    public List<Product> queryProductByCondition(QueryProduct queryProduct) {
        log.info("==========调用mcp工具:queryProductByCondition");
        log.info("==========参数mcp参数:queryProduct{}:", queryProduct);
        String productId = queryProduct.getProductId();
        String productName = queryProduct.getProductName();
        String brand = queryProduct.getBrand();
        Integer price = queryProduct.getPrice();
        Integer stock = queryProduct.getStock();
        Integer status = queryProduct.getStatus();
        ListSortEnum sortEnum = queryProduct.getSortEnum();
        PriceCompareEnum priceCompareEnum = queryProduct.getPriceCompareEnum();
        QueryWrapper  querywapper = new QueryWrapper();
        if (StringUtils.isNotBlank(productId)) {
            querywapper.eq("product_id", productId);
        }
        if (StringUtils.isNotBlank(productName)) {
            querywapper.like("product_name", productName);
        }
        if (StringUtils.isNotBlank(brand)) {
            querywapper.like("brand", brand);
        }
        if (status != null) {
            querywapper.eq("status", status);
        }
        if (price != null && priceCompareEnum != null) {
            if (priceCompareEnum.type.equals(PriceCompareEnum.GREATER_THAN.type)) {
                querywapper.gt("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.LESS_THAN.type)) {
                querywapper.lt("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.GREATER_THAN_OR_EQUAL_TO.type)) {
                querywapper.ge("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.LESS_THAN_OR_EQUAL_TO.type)) {
                querywapper.le("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.HIGHER_THAN.type)) {
                querywapper.gt("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.LOWER_THAN.type)) {
                querywapper.lt("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.NOT_HIGHER_THAN.type)) {
                querywapper.le("price", price);
            } else if (priceCompareEnum.type.equals(PriceCompareEnum.NOT_LOWER_THAN.type)) {
                querywapper.ge("price", price);
            } else {
                querywapper.eq("price", price);
            }
        }

        if (sortEnum != null && sortEnum.type.equals(ListSortEnum.ASC.type)) {
            querywapper.orderByAsc("price");
        }
        if (sortEnum != null && sortEnum.type.equals(ListSortEnum.DESC.type)) {
            querywapper.orderByDesc("price");
        }
        List<Product> products = productMapper.selectList(querywapper);
        log.info("==========调用mcp工具:queryProductByCondition  END");
        return products;
    }
    @Transactional
    @Tool(description = "根据商品ID修改商品信息，productId是商品ID，productName是商品名称，brand是品牌，description是描述，price是价格，stock是库存，status是状态")
    public String updateProduct(
            @ToolParam(description = "商品ID") String productId,
            @ToolParam(description = "商品名称") String productName,
            @ToolParam(description = "品牌") String brand,
            @ToolParam(description = "描述") String description,
            @ToolParam(description = "价格") Integer price,
            @ToolParam(description = "库存") Integer stock,
            @ToolParam(description = "状态") Integer status
    ) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setBrand(brand);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return "商品信息修改成功";
    }
}
