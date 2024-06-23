import { Option } from "effect"
import { LazyArg } from "effect/Function"
import { Identifiable } from ".";

export class CacheMap<K, V> extends Map<K, V> {
    getOrNone = (key: K) => Option.fromNullable(this.get(key))

    getOrElse = (key: K, onElse: LazyArg<V>) => Option.getOrElse(this.getOrNone(key), onElse)
}

export class CacheListMap<K, V> extends CacheMap<K, Array<V>> {
    addTo(key: K, value: V): void {
        let array = this.getOrElse(key, () => {
            let newArray = Array<V>()
            this.set(key, newArray)
            return newArray
        });
        array.push(value)
    }

    addAllTo(key: K, values: Array<V>): void {
        let existingArray = this.getOrNone(key);
        Option.match(existingArray, {
            onNone: () => this.set(key, values),
            onSome: (array) => values.map(value => array.push(value))
        })
    }

    removeAllFrom(key: K, values: Array<V>): void {
        Option.map(this.getOrNone(key), array => {
            let remaining = array.filter(e => !values.includes(e))
            this.set(key, remaining)
        })
    }
    
    remove(value: V): void {
        for (const [key, values] of this.entries()) {
            const i = values.findIndex(e => e === value)
            if (i > -1) values.splice(i, 1)
        }
    }

    getOrEmpty = (key: K) => this.get(key) ?? new Array<V>
}
