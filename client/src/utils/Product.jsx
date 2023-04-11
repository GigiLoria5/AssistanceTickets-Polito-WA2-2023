
function Product(id, asin, brand, category, manufacturerNumber, name, price,weight) {
    this.id = id;
    this.asin = asin;
    this.brand = brand;
    this.category = category;
    this.manufacturerNumber = manufacturerNumber;
    this.name = name;
    this.price = price;
    this.weight = weight;
}

exports.Product = Product;