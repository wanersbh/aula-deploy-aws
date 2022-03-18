import { ReactComponent as ArrowIcon } from 'assets/images/arrow.svg'
import ProductPrice from 'components/ProductPrice';

import './styles.css';

const ProductDetails = () => {
    return (
        <div className="product-details-container">
            <div className="base-card product-details-card">
                <div className="goback-container">
                    <ArrowIcon />
                    <h2>VOLTAR</h2>
                </div>
                <div className="row">
                    <div className="col-xl-6">
                        <div className="img-container">
                            <img src='https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg' alt="Nome do Produto" />
                        </div>
                        <div className="name-price-container">
                            <h1>Nome do produto</h1>
                            <ProductPrice price={2987.09} />
                        </div>

                    </div>
                    <div className="col-xl-6">
                        <div className="description-container">
                            <h2>Descrição do produto</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Autem minus distinctio assumenda, numquam, tempore ipsa repellendus cupiditate mollitia nostrum recusandae fuga nisi ullam quos molestias magnam, provident quidem vero nihil.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductDetails;